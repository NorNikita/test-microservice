package ru.pflb.boomq.test.args.provider;

import ru.pflb.boomq.model.testplan.Step;

public class StepProvider {

    public static Step getStep() {
        return Step.builder()
                .startThreadsCount(5)
                .initDelaySec(0L)
                .holdLoadTimeSec(300L)
                .shutDownTimeSec(0L)
                .build();
    }
}
