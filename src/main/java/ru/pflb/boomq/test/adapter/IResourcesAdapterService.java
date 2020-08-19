package ru.pflb.boomq.test.adapter;

import ru.pflb.boomq.model.testrunner.AllocatedResourceDto;
import ru.pflb.boomq.model.testrunner.Server;
import ru.pflb.boomq.test.model.entity.Resource;
import ru.pflb.boomq.test.model.entity.Test;

import java.util.List;

public interface IResourcesAdapterService {

    List<AllocatedResourceDto> allocateResources(Test test);

    Server createServer(Resource resource);
}
