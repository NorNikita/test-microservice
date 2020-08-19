package ru.pflb.boomq.test.statemachine.actions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import ru.pflb.boomq.model.test.TestState;
import ru.pflb.boomq.model.testrunner.AllocatedResourceDto;
import ru.pflb.boomq.model.testrunner.ContainerDto;
import ru.pflb.boomq.test.adapter.ITestRunnerAdapterService;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.repository.TestRepository;
import ru.pflb.boomq.test.statemachine.enums.Events;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
public class RunAction implements Action<TestState, Events> {

    private ITestRunnerAdapterService runnerAdapter;
    private TestRepository testRepository;

    public RunAction(ITestRunnerAdapterService runnerAdapter,
                     TestRepository testRepository) {
        this.runnerAdapter = runnerAdapter;
        this.testRepository = testRepository;
    }

    @Override
    public void execute(StateContext<TestState, Events> context) {
        Test test = context.getExtendedState().get("test", Test.class);

        Object objectContainers = context.getExtendedState().get("containers", Object.class);
        List<ContainerDto> containers= new ArrayList<>((Collection<ContainerDto>) objectContainers);

        Object objectResources = context.getExtendedState().get("allocatedResources", Object.class);
        List<AllocatedResourceDto> allocateResources = new ArrayList<>((Collection<AllocatedResourceDto>) objectResources);

        try {
            log.info("try run test with id = {}", test.getTestId());

            runnerAdapter.createContainersAndRun(containers, allocateResources, test);

            test.setState(TestState.RUNNING);
            test.setFromDate(LocalDateTime.now(ZoneOffset.UTC));
            test.setToDate(LocalDateTime.now(ZoneOffset.UTC).plusSeconds(test.getTotalDuration()));
            testRepository.save(test);

            log.info("test with id {} is running!", test.getTestId());

        } catch(Exception e) {
            context.getExtendedState().getVariables().put("next", TestState.FAILED_RUN_TEST);
            log.error("failed run test with id = {} failed! exceptionMessage: {}", test.getTestId(), e.getMessage());
        }
    }
}
