package ru.pflb.boomq.test.args.provider;

import ru.pflb.boomq.model.testplan.TestPlanReferencePrivateDto;
import ru.pflb.boomq.model.testplan.enums.FileExtension;
import ru.pflb.boomq.model.testplan.enums.RestrictionsLevel;
import ru.pflb.boomq.model.testplan.enums.TestingTool;

public class TestPlanReferencePrivateDtoProvider {

    public static TestPlanReferencePrivateDto getTestPlanReferencePrivateDto() {
        return TestPlanReferencePrivateDto.builder()
                .ownerId(1L)
                .fileLink("mi://boomq/users/1/1/828d095e-676a-4091-8bab-f3d392d438ca.jmx")
                .restrictionsLevel(RestrictionsLevel.INTERNAL)
                .fileExtension(FileExtension.JMX)
                .tool(TestingTool.JMETER)
                .build();
    }
}
