package ru.pflb.boomq.test.adapter;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pflb.boomq.model.testrunner.ContainerDto;
import ru.pflb.boomq.model.testrunner.ContainerState;
import ru.pflb.boomq.model.testrunner.AllocatedResourceDto;
import ru.pflb.boomq.model.testrunner.State;
import ru.pflb.boomq.test.adapter.testrunner.TestRunnerAdapterService;
import ru.pflb.boomq.test.iface.TestRunnerClient;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.model.entity.UsedResource;
import ru.pflb.boomq.test.repository.UsedResourceRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TestRunnerAdapterServiceTest {

    @Mock
    private TestRunnerClient testRunnerClient;

    @Mock
    private UsedResourceRepository usedResourceRepository;

    @InjectMocks
    private TestRunnerAdapterService testRunnerAdapterService;

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.adapter.TestRunnerAdapterServiceStreamArgs#getArgsFor_stopContainers")
    void stopContainers(Test test,
                        List<UsedResource> usedResource) {

        when(usedResourceRepository.findAllByTest(test)).thenReturn(usedResource);

        testRunnerAdapterService.stopTestContainer(test);

        usedResource.forEach(resource -> assertEquals(State.CANCELED, resource.getState()));
    }

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.adapter.TestRunnerAdapterServiceStreamArgs#getArgsFor_createContainersAndRun")
    void createContainersAndRun(List<ContainerDto> containers,
                                List<AllocatedResourceDto> allocatedResources,
                                ContainerState containerState,
                                Test test) {

        containers.forEach(container ->
                when(testRunnerClient.createContainer(container.getServerId(), container)).thenReturn(container)
        );
        containers.forEach(container ->
                when(testRunnerClient.startContainer(container.getServerId(), container.getId())).thenReturn(containerState)
        );

        testRunnerAdapterService.createContainersAndRun(containers, allocatedResources, test);
    }
}