package ru.pflb.boomq.test.args.provider;

import ru.pflb.boomq.model.testplan.Scenario;

import java.util.Collections;

public class ScenarioProvider {

    public static Scenario getScenario() {
        return Scenario.builder()
                .steps(Collections.singletonList(StepProvider.getStep()))
                .groups(TestProfileDtoProvider.getTestProfileDto().getGroups())
                .parameters(TestSettingDtoProvider.getTestSettingDto().getParameters())
                .build();
    }
}