package ru.pflb.boomq.test.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pflb.boomq.model.testrunner.ContainerDto;
import ru.pflb.boomq.model.testrunner.AllocatedResourceDto;
import ru.pflb.boomq.model.test.TestState;
import ru.pflb.boomq.model.test.exception.TestServiceException;
import ru.pflb.boomq.model.test.settings.TestSettingDto;
import ru.pflb.boomq.test.adapter.IContainerAdapterService;
import ru.pflb.boomq.test.adapter.IResourcesAdapterService;
import ru.pflb.boomq.test.adapter.ITestRunnerAdapterService;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.model.entity.TestPlan;
import ru.pflb.boomq.test.repository.TestPlanRepository;
import ru.pflb.boomq.test.repository.TestRepository;
import ru.pflb.boomq.test.repository.UsedResourceRepository;
import ru.pflb.boomq.test.utils.Builder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TestWaitScheduler {

    private IContainerAdapterService containerAdapter;
    private IResourcesAdapterService resourceAdapter;
    private ITestRunnerAdapterService runnerAdapter;
    private TestRepository testRepository;
    private TestPlanRepository testPlanRepository;
    private UsedResourceRepository usedResourceRepository;
    private ObjectMapper objectMapper;
    private Builder builder;

    public TestWaitScheduler(IContainerAdapterService containerAdapter,
                             IResourcesAdapterService resourceAdapter,
                             ITestRunnerAdapterService runnerAdapter,
                             TestRepository testRepository,
                             TestPlanRepository testPlanRepository,
                             UsedResourceRepository usedResourceRepository,
                             @Qualifier("objectMapper") ObjectMapper objectMapper,
                             Builder builder) {
        this.containerAdapter = containerAdapter;
        this.resourceAdapter = resourceAdapter;
        this.runnerAdapter = runnerAdapter;
        this.testRepository = testRepository;
        this.testPlanRepository = testPlanRepository;
        this.usedResourceRepository = usedResourceRepository;
        this.objectMapper = objectMapper;
        this.builder = builder;
    }

    @Transactional
    @Scheduled(fixedRate = 50000)
    public void tryGetResourcesAndRun() {
        List<Test> waitingTests = testRepository.findTestByState(TestState.WAITING_RESOURCES);

        log.info("attempt to allocate resource for waiting tests and run them! count waiting tests: {}", waitingTests.size());

        try{
            List<Test> updatedTest = waitingTests
                    .stream()
                    .peek(test -> {
                        List<AllocatedResourceDto> allocateResources;
                        try {
                            allocateResources = resourceAdapter.allocateResources(test);

                            if (allocateResources != null) {
                                log.info("TestWaitScheduler: resources for test with id = {} successful allocated!", test.getTestId());
                                test.setState(TestState.RESOURCES_ALLOCATED);
                            } else {
                                return;
                            }

                        } catch (TestServiceException e) {
                            log.error("fail in process allocated resources! test with id = {}", test.getTestId());
                            test.setState(TestState.ERROR_RESOURCES_ALLOCATED);
                            return;
                        }

                        List<ContainerDto> containers;
                        try{
                            TestSettingDto testSettingDto = objectMapper.readValue(test.getSettings(), TestSettingDto.class);
                            TestPlan testPlan = testPlanRepository.findByTestId(test.getTestId());
                            containers = containerAdapter.buildContainers(allocateResources, test, testPlan, testSettingDto);

                        } catch (TestServiceException | IOException e) {
                            log.error("create container for test with id = {} failed!", test.getTestId());
                            return;
                        }

                        try {
                            runnerAdapter.createContainersAndRun(containers, allocateResources, test);
                            test.setState(TestState.RUNNING);
                            test.setFromDate(LocalDateTime.now(ZoneOffset.UTC));
                            test.setToDate(LocalDateTime.now(ZoneOffset.UTC).plusSeconds(test.getTotalDuration()));

                        } catch(Exception e) {
                            log.error("failed run test with id = {}", test.getTestId());
                            test.setState(TestState.FAILED_RUN_TEST);
                        }

                        log.info("test with id {} run!", test.getTestId());
                    })
                    .filter(test -> test.getState() != TestState.WAITING_RESOURCES)
                    .collect(Collectors.toList());

            if(!updatedTest.isEmpty()) testRepository.saveAll(updatedTest);

            String result = updatedTest
                    .stream()
                    .map(test -> " testId: " + test.getTestId() + " state:" + test.getState() + "\n")
                    .reduce((u, v) -> u + v).orElse("nothing update");

            log.info("result work of scheduler:\n {}", result);

        } catch (Exception e) {
            log.error("error in process to allocate resources! message: {}", e.getMessage());
        }
    }
}
