package ru.pflb.boomq.test.adapter;

import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.model.entity.TestPlan;

public interface ITestPlanAdapterService {

    TestPlan createTestPlan(Test test) throws Exception;

}
