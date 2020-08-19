package ru.pflb.boomq.test.args.statemachine;

import org.junit.jupiter.params.provider.Arguments;
import ru.pflb.boomq.model.test.TestState;
import ru.pflb.boomq.test.args.provider.*;

import java.util.Arrays;
import java.util.stream.Stream;

public class StateMachineStreamArgs {

    public static Stream<Arguments> getArgsFor_initializeAction() {
        return Stream.of(
                Arguments.of(
                        TestProvider.getTest(TestState.CREATED),
                        ContentProvider.getContent(),
                        TestSettingDtoProvider.getTestSettingDto(),
                        TestProfileDtoProvider.getTestProfileDto()
                )
        );
    }

    public static Stream<Arguments> getArgsFor_getTestPlanAction() {
        return Stream.of(
                Arguments.of(
                        TestProvider.getTest(TestState.INITIALIZATION),
                        TestPlanProvider.createTestPlan()
                )
        );
    }

    public static Stream<Arguments> getArgsFor_allocateResourceAction() {
        return Stream.of(
                Arguments.of(
                        TestProvider.getTest(TestState.TEST_PLAN_CREATED),
                        AllocatedResourceDtoProvider.getListAllocatedResourceDto()
                )
        );
    }

    public static Stream<Arguments> getArgsFor_buildContainersAction() {
        return Stream.of(
                Arguments.of(
                        TestProvider.getTest(TestState.RESOURCES_ALLOCATED),
                        AllocatedResourceDtoProvider.getListAllocatedResourceDto(),
                        TestPlanProvider.createTestPlan(),
                        TestSettingDtoProvider.getTestSettingDto(),
                        Arrays.asList(ContainerDtoProvider.getContainerDto())
                )
        );
    }

    public static Stream<Arguments> getArgsFor_runAction() {
        return Stream.of(
                Arguments.of(
                        TestProvider.getTest(TestState.BUILD_CONTAINERS),
                        Arrays.asList(
                                ContainerDtoProvider.getContainerDto()
                        ),
                        Arrays.asList(
                                AllocatedResourceDtoProvider.getAllocatedResourceDto()
                        )
                )
        );
    }
}