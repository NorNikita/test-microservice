package ru.pflb.boomq.test.args.provider;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.pflb.boomq.model.projectservice.dto.testproject.TestType;
import ru.pflb.boomq.model.test.TestDto;
import ru.pflb.boomq.model.test.TestState;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

public class TestDtoProvider {

    public static TestDto getRunningTestDto() {
        return TestDto.builder()
                .testId(1L)
                .projectId(1L)
                .userId(1L)
                .state(TestState.RUNNING)
                .countUsers(1L)
                .type(TestType.STABLE)
                .comment("Some important comment!")
                .totalDuration(300L)
                .fromDate(LocalDateTime.now(ZoneOffset.UTC))
                .toDate(LocalDateTime.now(ZoneOffset.UTC).plusSeconds(300))
                .build();
    }

    public static TestDto getStoppedTestDto() {
        return TestDto.builder()
                .testId(1L)
                .projectId(1L)
                .userId(1L)
                .state(TestState.STOPPED)
                .countUsers(5L)
                .type(TestType.STABLE)
                .comment("Some important comment!")
                .totalDuration(360L)
                .fromDate(LocalDateTime.now(ZoneOffset.UTC))
                .toDate(LocalDateTime.now(ZoneOffset.UTC).plusSeconds(360))
                .build();
    }

    public static Page<TestDto> getPageTestDto() {
        return new PageImpl<>(Arrays.asList(getRunningTestDto()));
    }
}