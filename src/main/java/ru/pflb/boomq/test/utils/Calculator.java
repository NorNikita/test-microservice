package ru.pflb.boomq.test.utils;

import org.springframework.stereotype.Component;
import ru.pflb.boomq.model.testplan.Step;
import ru.pflb.boomq.model.test.settings.TestSettingDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class Calculator {

    public TestSettingDto calculationTimeDuration(TestSettingDto dto) {
        TestSettingDto result = dto.toBuilder()
                .stepCount(Optional.ofNullable(dto.getStepCount()).orElse(0))
                .rampUp(Optional.ofNullable(dto.getRampUp()).orElse(0L))
                .rampDown(Optional.ofNullable(dto.getRampDown()).orElse(0L))
                .durationAddedOnLastStep(Optional.ofNullable(dto.getDurationAddedOnLastStep()).orElse(0L))
                .build();

        long totalDuration = result.getStepCount() * (result.getStepLength() + result.getRampUp())
                           + result.getRampDown()
                           + result.getDurationAddedOnLastStep();

        dto.setTotalDuration(totalDuration);
        return dto;
    }

    public List<Step> calculateStep(TestSettingDto settingDto) {

        Integer stepCount = settingDto.getStepCount();
        Long stepLength = settingDto.getStepLength();
        Long rampUp = settingDto.getRampUp();
        Long rampDown = settingDto.getRampDown();
        Integer usersPerStep = settingDto.getUsersPerStep();
        Long totalDuration = settingDto.getTotalDuration();

        List<Step> steps = new ArrayList<>(stepCount);

        for(int i = 0; i < stepCount; i++) {
            Long initDelaySec = (rampUp + stepLength) * i;
            Long holdLoadTimeSec = (totalDuration - rampUp - rampDown) - i * (rampUp + stepLength);

            steps.add(Step.builder()
                    .startThreadsCount(usersPerStep)
                    .initDelaySec(initDelaySec)
                    .holdLoadTimeSec(holdLoadTimeSec)
                    .shutDownTimeSec(rampDown)
                    .build());
        }

        return steps;
    }
}
