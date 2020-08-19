package ru.pflb.boomq.test.service;

import ru.pflb.boomq.model.testrunner.ResourceDto;

public interface IResourceService {

    Long createResource(ResourceDto resourceDto);

    ResourceDto getResource(Long id);
}