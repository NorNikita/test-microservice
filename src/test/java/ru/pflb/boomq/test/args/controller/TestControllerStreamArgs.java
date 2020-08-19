package ru.pflb.boomq.test.args.controller;


import org.junit.jupiter.params.provider.Arguments;
import ru.pflb.boomq.model.test.TestState;
import ru.pflb.boomq.test.args.provider.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

public class TestControllerStreamArgs {

    public static Stream<Arguments> getParamsForRunTest() throws Exception {
        return Stream.of(
                Arguments.of(
                        JwtProvider.getJwt(),
                        AuthorityProvider.getUserAuthority(),
                        TestEventDtoProvider.getTestEventDTO(),
                        ContentProvider.getContent(),
                        TestPlanProvider.createTestPlan(),
                        Arrays.asList(
                                AllocatedResourceDtoProvider.getAllocatedResourceDto()
                        ),
                        Arrays.asList(
                                ContainerDtoProvider.getContainerDto()
                        )
                )
        );
    }

    public static Stream<Arguments> getParams() throws IOException {
        return Stream.of(
                Arguments.of(JwtProvider.getJwt(), AuthorityProvider.getUserAuthority(), TestProvider.getTest(TestState.INITIALIZATION))
        );
    }
}