package ru.pflb.boomq.test.args.adapter;

import org.junit.jupiter.params.provider.Arguments;
import ru.pflb.boomq.model.test.TestState;
import ru.pflb.boomq.test.args.provider.*;

import java.util.Arrays;
import java.util.stream.Stream;

public class TestRunnerAdapterServiceStreamArgs {

    public static Stream<Arguments> getArgsFor_stopContainers() {
        return Stream.of(
                Arguments.of(
                        TestProvider.getTest(TestState.RUNNING),
                        Arrays.asList(UsedResourceProvider.getUsedResource(1L))
                )
        );
    }

    public static Stream<Arguments> getArgsFor_createContainersAndRun() {
        return Stream.of(
                Arguments.of(
                        Arrays.asList(ContainerDtoProvider.getContainerDto()),
                        Arrays.asList(AllocatedResourceDtoProvider.getAllocatedResourceDto()),
                        ContainerStateProvider.getSuccessfulContainerState(),
                        TestProvider.getTest(TestState.BUILD_CONTAINERS)
                )
        );
    }
}