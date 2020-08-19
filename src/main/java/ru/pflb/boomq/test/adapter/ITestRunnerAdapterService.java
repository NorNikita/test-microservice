package ru.pflb.boomq.test.adapter;

import ru.pflb.boomq.model.testrunner.ContainerDto;
import ru.pflb.boomq.model.testrunner.AllocatedResourceDto;
import ru.pflb.boomq.test.model.entity.Test;

import java.util.List;

public interface ITestRunnerAdapterService {

    void stopTestContainer(Test test);

    void createContainersAndRun(List<ContainerDto> containers,
                                List<AllocatedResourceDto> allocatedResource,
                                Test test) throws Exception;
}
