package ru.pflb.boomq.test.args.adapter;

import org.junit.jupiter.params.provider.Arguments;
import ru.pflb.boomq.model.test.TestState;
import ru.pflb.boomq.test.args.provider.*;

import java.util.Arrays;
import java.util.stream.Stream;

public class TestPlanAdapterServiceArgs {

    public static Stream<Arguments> getArgsFor_createTestPlan() {
        return Stream.of(
                Arguments.of(TestProvider.getTest(TestState.INITIALIZATION),
                        TestSettingDtoProvider.getTestSettingDto(),
                        TestProfileDtoProvider.getTestProfileDto(),
                        TestPlanReferencePrivateDtoProvider.getTestPlanReferencePrivateDto(),
                        Arrays.asList(StepProvider.getStep()),
                        TestPlanProvider.createTestPlan(),
                        ScenarioProvider.getScenario()));
    }
}