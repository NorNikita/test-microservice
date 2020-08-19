package ru.pflb.boomq.test.args.provider;

import ru.pflb.boomq.model.projectservice.dto.testproject.TestType;
import ru.pflb.boomq.model.test.TestEventDto;
import ru.pflb.boomq.model.test.TestState;

public class TestEventDtoProvider {

    public static TestEventDto getTestEventDTO() {
        return TestEventDto.builder()
                .projectId(1L)
                .bucketUri("mi://boomq/users/300/300/untitled_project_19:39.yaml")
                .comment("Some important comment!")
                .state(TestState.CREATED)
                .type(TestType.STABLE)
                .version("1.0.0")
                .build();
    }
}