package ru.pflb.boomq.test.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pflb.boomq.model.testrunner.ResourceDto;
import ru.pflb.boomq.model.testrunner.Server;
import ru.pflb.boomq.test.adapter.IResourcesAdapterService;
import ru.pflb.boomq.test.model.entity.Resource;
import ru.pflb.boomq.test.repository.ResourceRepository;
import ru.pflb.boomq.test.service.impl.ResourceServiceImpl;
import ru.pflb.boomq.test.utils.Builder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private Builder builder;

    @Mock
    private IResourcesAdapterService resourcesAdapter;

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.service.ResourceServiceStreamArgs#getArgsFor_createResource")
    void createResource(ResourceDto resourceDto,
                        Resource resource,
                        Server server) {
        when(builder.buildResource(resourceDto)).thenReturn(resource);
        when(resourceRepository.save(resource)).thenReturn(resource);
        when(resourcesAdapter.createServer(resource)).thenReturn(server);

        Long id = resourceService.createResource(resourceDto);

        assertEquals(resource.getId(), id);
    }

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.service.ResourceServiceStreamArgs#getArgsFor_getResource")
    void getResource(Resource resource,
                     ResourceDto resourceDto) {

        when(resourceRepository.findById(resource.getId())).thenReturn(Optional.of(resource));
        when(builder.buildResourceDto(resource)).thenReturn(resourceDto);

        ResourceDto test = resourceService.getResource(resource.getId());

        assertEquals(resourceDto.getServerId(), test.getServerId());
    }
}