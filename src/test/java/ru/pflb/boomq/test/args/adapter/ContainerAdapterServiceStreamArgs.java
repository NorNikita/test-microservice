package ru.pflb.boomq.test.args.adapter;

import org.junit.jupiter.params.provider.Arguments;
import ru.pflb.boomq.model.test.TestState;
import ru.pflb.boomq.test.args.provider.*;

import java.util.stream.Stream;

public class ContainerAdapterServiceStreamArgs {

    public static Stream<Arguments> getArgsFor_buildContainers() {
        return Stream.of(
                Arguments.of(
                        AllocatedResourceDtoProvider.getListAllocatedResourceDto(),
                        TestProvider.getTest(TestState.RESOURCES_ALLOCATED),
                        TestPlanProvider.createTestPlan(),
                        TestSettingDtoProvider.getTestSettingDto(),
                        InfluxUserDtoProvider.getInfluxUserDto()
                )
        );
    }
}