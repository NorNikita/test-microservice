package ru.pflb.boomq.test.adapter.testrunner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pflb.boomq.model.testrunner.ContainerDto;
import ru.pflb.boomq.model.testrunner.ContainerState;
import ru.pflb.boomq.model.testrunner.AllocatedResourceDto;
import ru.pflb.boomq.model.testrunner.State;
import ru.pflb.boomq.test.adapter.ITestRunnerAdapterService;
import ru.pflb.boomq.test.iface.TestRunnerClient;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.model.entity.UsedResource;
import ru.pflb.boomq.test.repository.UsedResourceRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TestRunnerAdapterService implements ITestRunnerAdapterService {

    private TestRunnerClient testRunnerClient;
    private UsedResourceRepository usedResourceRepository;

    public TestRunnerAdapterService(TestRunnerClient testRunnerClient,
                                    UsedResourceRepository usedResourceRepository) {
        this.testRunnerClient = testRunnerClient;
        this.usedResourceRepository = usedResourceRepository;
    }

    @Override
    public void stopTestContainer(Test test) {
        log.info("try stop all containers if test with id {}", test.getTestId());

        List<UsedResource> stoppedResource = usedResourceRepository.findAllByTest(test)
                .stream()
                .peek(resource -> {
                    testRunnerClient.stopContainer(resource.getServerId(), resource.getContainerId());
                    resource.setState(State.CANCELED);
                }).collect(Collectors.toList());

        usedResourceRepository.saveAll(stoppedResource);
    }

    @Override
    public void createContainersAndRun(List<ContainerDto> containers,
                                       List<AllocatedResourceDto> allocatedResources,
                                       Test test) {
        log.info("try create containers for run test with id = {}!", test.getTestId());

        Map<String, AllocatedResourceDto> allocatedResourceMap = new HashMap<>();
        allocatedResources.forEach(resource -> allocatedResourceMap.put(resource.getServerId(), resource));

        Map<String, String> createdContainers = new HashMap<>();
        containers.forEach(container -> {
            ContainerDto createdContainer = testRunnerClient.createContainer(container.getServerId(), container);
            createdContainers.put(createdContainer.getId(), container.getServerId());
        });

        log.info("containers for test with id = {} was created. count containers: {}." +
                " now try to run them!", test.getTestId(), createdContainers.size());

        List<ContainerState> stateLaunchedContainers = createdContainers.entrySet()
                .stream()
                .map(container -> testRunnerClient.startContainer(container.getValue(), container.getKey()))
                .collect(Collectors.toList());

        log.info("containers for test with id {} successful launched!\n state containers: {}" , test.getTestId(), stateLaunchedContainers.toString());

        createdContainers.entrySet().forEach(container -> {

            AllocatedResourceDto allocatedResource = allocatedResourceMap.get(container.getValue());

            UsedResource usedResource = UsedResource.builder()
                    .containerId(container.getKey())
                    .serverId(allocatedResource.getServerId())
                    .countUsers(allocatedResource.getAllocatedCountUser())
                    .test(test)
                    .state(State.EXECUTABLE)
                    .build();

            usedResourceRepository.save(usedResource);
        });
    }
}
