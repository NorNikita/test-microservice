package ru.pflb.boomq.test.args.service;


import org.junit.jupiter.params.provider.Arguments;
import org.springframework.data.domain.PageRequest;
import ru.pflb.boomq.model.test.TestState;
import ru.pflb.boomq.test.args.provider.JwtProvider;
import ru.pflb.boomq.test.args.provider.TestDtoProvider;
import ru.pflb.boomq.test.args.provider.TestEventDtoProvider;
import ru.pflb.boomq.test.args.provider.TestProvider;
import ru.pflb.boomq.test.model.entity.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

public class TestServiceStreamArgs {

    public static Stream<Arguments> getArgsFor_createTest() throws IOException {
        return Stream.of(
                Arguments.of(
                        JwtProvider.getJwt(),
                        TestProvider.getTest(TestState.CREATED),
                        TestEventDtoProvider.getTestEventDTO()
                )
        );
    }

    public static Stream<Arguments> getArgsFor_updateTest() throws IOException {
        return Stream.of(
                Arguments.of(
                        JwtProvider.getJwt(),
                        TestProvider.getTest(TestState.RUNNING),
                        TestDtoProvider.getStoppedTestDto()
                )
        );
    }

    public static Stream<Arguments> getArgsFor_getTest() throws IOException {
        return Stream.of(
                Arguments.of(
                        JwtProvider.getJwt(),
                        TestProvider.getTest(TestState.RUNNING),
                        TestDtoProvider.getRunningTestDto()
                )
        );
    }

    public static Stream<Arguments> getArgsFor_getUserTests() {
        return Stream.of(
                Arguments.of(
                        1L,
                        PageRequest.of(0, 10),
                        TestProvider.getPageTest(),
                        TestDtoProvider.getPageTestDto()
                )
        );
    }

    public static Stream<Arguments> getLastServerTime() {
        Test test = TestProvider.getTest(TestState.AFTER_CREATED);
        LocalDateTime fromDate = LocalDateTime.parse("2020-01-01T00:00:00");
        test.setFromDate(fromDate);
        return Stream.of(
                Arguments.of(Optional.of(test), Instant.parse("2020-01-01T00:00:00.000Z")),
                Arguments.of(Optional.empty(), Instant.ofEpochMilli(0L))
        );
    }

}
