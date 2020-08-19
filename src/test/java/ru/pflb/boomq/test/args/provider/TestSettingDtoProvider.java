package ru.pflb.boomq.test.args.provider;

import ru.pflb.boomq.model.projectservice.dto.testproject.TestType;
import ru.pflb.boomq.model.test.settings.TestSettingDto;

public class TestSettingDtoProvider {

    public static TestSettingDto getTestSettingDto() {
        return TestSettingDto.builder()
                .stepCount(5)
                .usersPerStep(5)
                .testType(TestType.STABLE)
                .totalDuration(300L)
                .build();
    }
}