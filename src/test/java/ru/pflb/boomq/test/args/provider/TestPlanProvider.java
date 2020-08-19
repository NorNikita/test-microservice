package ru.pflb.boomq.test.args.provider;

import ru.pflb.boomq.model.testplan.enums.FileExtension;
import ru.pflb.boomq.model.testplan.enums.RestrictionsLevel;
import ru.pflb.boomq.model.testplan.enums.TestingTool;
import ru.pflb.boomq.test.model.entity.TestPlan;

public class TestPlanProvider {

    public static TestPlan createTestPlan() {
        return TestPlan.builder()
                .ownerId(1L)
                .jmxUri("mi://boomq/users/1/1/828d095e-676a-4091-8bab-f3d392d438ca.jmx")
                .fileName("828d095e-676a-4091-8bab-f3d392d438ca.jmx")
                .restrictionsLevel(RestrictionsLevel.EXTERNAL)
                .testingTool(TestingTool.JMETER)
                .fileExtension(FileExtension.JMX)
                .build();
    }
}
