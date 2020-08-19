package ru.pflb.boomq.test.args.provider;

import ru.pflb.boomq.model.testrunner.ContainerState;

import java.util.Random;

public class ContainerStateProvider {

    public static ContainerState getSuccessfulContainerState() {
        return ContainerState.builder()
                .status("running")
                .running(true)
                .paused(false)
                .restarting(false)
                .oomKilled(false)
                .dead(false)
                .pid(new Random().nextInt(10000))
                .exitCode(0)
                .build();
    }
}
