package ru.pflb.boomq.test.adapter;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pflb.boomq.model.testrunner.AllocatedResourceDto;
import ru.pflb.boomq.model.testrunner.FreeResourceDto;
import ru.pflb.boomq.model.testrunner.Server;
import ru.pflb.boomq.model.testrunner.State;
import ru.pflb.boomq.test.adapter.testrunner.ResourcesAdapterService;
import ru.pflb.boomq.test.iface.TestRunnerClient;
import ru.pflb.boomq.test.model.entity.Resource;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.model.entity.UsedResource;
import ru.pflb.boomq.test.repository.ResourceRepository;
import ru.pflb.boomq.test.repository.UsedResourceRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourcesAdapterServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private TestRunnerClient testRunnerClient;

    @Mock
    private UsedResourceRepository usedResourceRepository;

    @InjectMocks
    private ResourcesAdapterService resourcesAdapterService;

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.adapter.ResourceAdapterServiceStreamArgs#getArgsFor_allocateResources")
    void allocateResource(Test test,
                          List<Resource> resources,
                          List<FreeResourceDto> freeResourceDtos,
                          List<UsedResource> usedResources) {

        List<String> serverIds = resources.stream().map(Resource::getServerId).collect(Collectors.toList());
        List<String> availableServerIds = freeResourceDtos.stream().map(FreeResourceDto::getServerId).collect(Collectors.toList());

        when(resourceRepository.findAllByForFree(true)).thenReturn(resources);
        when(testRunnerClient.getResourcesStates(serverIds)).thenReturn(freeResourceDtos);
        when(usedResourceRepository.findAllByServerIdInAndState(availableServerIds, State.EXECUTABLE)).thenReturn(usedResources);

        List<AllocatedResourceDto> allocatedResourceDtoList = resourcesAdapterService.allocateResources(test);

        assertEquals(test.getCountUsers(), allocatedResourceDtoList.stream().map(AllocatedResourceDto::getAllocatedCountUser).reduce(Long::sum).get());
    }

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.adapter.ResourceAdapterServiceStreamArgs#getArgsFor_createServer")
    void createServer(Resource resource,
                      Server server) {

        when(testRunnerClient.createServer(any(Server.class))).thenReturn(server);

        Server createdServer = resourcesAdapterService.createServer(resource);

        assertEquals(createdServer.getId(), resource.getServerId());
    }
}
