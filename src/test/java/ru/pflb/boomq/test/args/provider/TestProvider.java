package ru.pflb.boomq.test.args.provider;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.pflb.boomq.model.projectservice.dto.testproject.TestType;
import ru.pflb.boomq.model.test.TestState;
import ru.pflb.boomq.test.model.entity.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

public class TestProvider {
    private static final String settings = "{\"stepCount\":1," +
            "\"usersPerStep\":5," +
            "\"stepLength\":300," +
            "\"durationAddedOnLastStep\":null," +
            "\"rampUp\":60," +
            "\"rampDown\":0," +
            "\"totalDuration\":360," +
            "\"parameters\":[]," +
            "\"testType\":\"STABLE\"," +
            "\"version\":\"1.0.0\"}";

    private static final String testPlan = "{\"groups\":[{\"requests\":[{\"url\":\"https://yandex.ru\",\"body\":\"\",\"headers\":{},\"params\":{},\"method\":\"GET\",\"extractors\":{}}],\"perc\":100.0,\"name\":\"New group 1\"}]}";

    public static Test getTest(TestState state) {
        return Test.builder()
                .testId(1L)
                .projectId(1L)
                .userId(1L)
                .settings("{\"stepCount\":1,\"usersPerStep\":5,\"stepLength\":300,\"durationAddedOnLastStep\":null,\"rampUp\":60,\"rampDown\":0,\"totalDuration\":360,\"parameters\":[],\"testType\":\"STABLE\",\"version\":\"1.0.0\"}")
                .bucketUri("mi://boomq/users/1/1/untitled_project_00:01.yaml")
                .state(state)
                .settings(settings)
                .testProfile(testPlan)
                .type(TestType.STABLE)
                .comment("Комментарий к текущему тесту")
                .countUsers(5L)
                .totalDuration(360L)
                .fromDate(LocalDateTime.now(ZoneOffset.UTC))
                .toDate(LocalDateTime.now(ZoneOffset.UTC).plusSeconds(360))
                .build();
    }

    public static Page<Test> getPageTest() {
        return new PageImpl<>(Arrays.asList(getTest(TestState.FINISHED), getTest(TestState.RUNNING), getTest(TestState.INITIALIZATION)));
    }
}
