package ru.pflb.boomq.test.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pflb.boomq.model.test.ExceptionMessage;
import ru.pflb.boomq.model.test.exception.TestServiceException;
import ru.pflb.boomq.model.testrunner.ResourceDto;
import ru.pflb.boomq.model.testrunner.Server;
import ru.pflb.boomq.test.adapter.IResourcesAdapterService;
import ru.pflb.boomq.test.model.entity.Resource;
import ru.pflb.boomq.test.repository.ResourceRepository;
import ru.pflb.boomq.test.service.IResourceService;
import ru.pflb.boomq.test.utils.Builder;

@Slf4j
@Service
public class ResourceServiceImpl implements IResourceService {

    private Builder builder;
    private IResourcesAdapterService resourcesAdapter;
    private ResourceRepository resourceRepository;

    public ResourceServiceImpl(Builder builder, IResourcesAdapterService resourcesAdapter, ResourceRepository resourceRepository) {
        this.builder = builder;
        this.resourcesAdapter = resourcesAdapter;
        this.resourceRepository = resourceRepository;
    }

    @Override
    public Long createResource(ResourceDto resourceDto) {
        log.info("add new resource! resourceDto: {}", resourceDto);

        Resource resource = resourceRepository.save(
                builder.buildResource(resourceDto)
        );

        Server server = resourcesAdapter.createServer(resource);

        log.info("new resource with id {} successful added & new server created: server {}", resource.getId(), server);
        return resource.getId();
    }

    @Override
    public ResourceDto getResource(Long id) {
        log.info("get resource with id: {}", id);

        Resource resource = resourceRepository.findById(id).orElseThrow(() -> new TestServiceException(ExceptionMessage.RESOURCE_NOT_FOUND));
        return builder.buildResourceDto(resource);
    }
}