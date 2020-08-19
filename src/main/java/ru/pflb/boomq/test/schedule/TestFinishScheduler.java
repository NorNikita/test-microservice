package ru.pflb.boomq.test.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.pflb.boomq.model.testrunner.ContainerState;
import ru.pflb.boomq.model.testrunner.State;
import ru.pflb.boomq.model.test.TestState;
import ru.pflb.boomq.test.iface.TestRunnerClient;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.model.entity.UsedResource;
import ru.pflb.boomq.test.repository.TestRepository;
import ru.pflb.boomq.test.repository.UsedResourceRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TestFinishScheduler {

    private TestRepository repository;
    private TestRunnerClient testRunnerClient;
    private UsedResourceRepository usedResourceRepository;

    public TestFinishScheduler(TestRepository repository,
                               TestRunnerClient testRunnerClient,
                               UsedResourceRepository usedResourceRepository) {
        this.repository = repository;
        this.testRunnerClient = testRunnerClient;
        this.usedResourceRepository = usedResourceRepository;
    }

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void checkRunningTestsWhichFinished() {
        log.info("begin check running test for completion");

        try {
            List<Test> runningTests = repository.findTestByState(TestState.RUNNING);

            List<Test> notRunning = runningTests.parallelStream()
                    .peek(test -> {

                        int running = 0, finished = 0, error = 0, closed = 0;
                        List<UsedResource> allUsedResources = usedResourceRepository.findAllByTest(test);

                        for(UsedResource resource : allUsedResources) {
                            ContainerState stateContainer = testRunnerClient.getStateContainer(resource.getServerId(), resource.getContainerId());

                            if(stateContainer.getRunning())  {
                                running++;
                                continue;
                            }

                            switch (stateContainer.getExitCode()) {
                                case 1: {
                                    error++;
                                    resource.setState(State.ERROR);
                                    usedResourceRepository.save(resource);
                                    break;
                                }
                                case 137: {
                                    closed++;
                                    resource.setState(State.CANCELED);
                                    usedResourceRepository.save(resource);
                                    break;
                                }
                                default: {
                                    finished++;
                                    resource.setState(State.FINISHED);
                                    usedResourceRepository.save(resource);
                                }
                            }
                        }

                        if(running > 0) {
                            if (closed > 0 || error > 0) {
                                test.setState(TestState.RUNNING_WITH_ERROR);
                            }
                        } else {
                            if(finished > 0) {
                                if(error > 0 || closed > 0) {
                                    test.setState(TestState.FINISHED_WITH_ERROR);
                                } else {
                                    test.setState(TestState.FINISHED);
                                }
                            } else if(closed > 0) {
                                test.setState(TestState.STOPPED);
                            }
                        }

                    })
                    .filter(test -> test.getState() != TestState.RUNNING)
                    .collect(Collectors.toList());

            if (!notRunning.isEmpty()) {
                repository.saveAll(notRunning);
            }

            log.info("end check running test for completion. count finished test: {}\n", notRunning.size());

        } catch (Exception e) {
            log.error("error in process check running test for completion! error message: {}\n", e.getMessage());
        }

    }

}