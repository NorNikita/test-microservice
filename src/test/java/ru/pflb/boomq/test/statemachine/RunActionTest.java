package ru.pflb.boomq.test.statemachine;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import ru.pflb.boomq.model.test.TestState;
import ru.pflb.boomq.model.testrunner.AllocatedResourceDto;
import ru.pflb.boomq.model.testrunner.ContainerDto;
import ru.pflb.boomq.test.adapter.ITestRunnerAdapterService;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.repository.TestRepository;
import ru.pflb.boomq.test.statemachine.actions.RunAction;
import ru.pflb.boomq.test.statemachine.enums.Events;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RunActionTest {

    @Mock
    private ITestRunnerAdapterService runnerAdapter;

    @Mock
    private TestRepository testRepository;

    @Mock
    private StateContext<TestState, Events> context;

    @Mock
    private ExtendedState extendedState;

    @InjectMocks
    private RunAction runAction;

    //CREATED -> INITIALIZATION -> TEST_PLAN_CREATED -> RESOURCE_ALLOCATED -> BUILD_CONTAINERS -> RUNNING
    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.statemachine.StateMachineStreamArgs#getArgsFor_runAction")
    void buildContainers(Test test,
                         List<ContainerDto> containerDtos,
                         List<AllocatedResourceDto> allocatedResourceDtos) throws Exception {

        when(context.getExtendedState()).thenReturn(extendedState);
        when(extendedState.get("test", Test.class)).thenReturn(test);
        when(extendedState.get("allocatedResources", Object.class)).thenReturn(allocatedResourceDtos);
        when(extendedState.get("containers", Object.class)).thenReturn(containerDtos);

        doNothing().when(runnerAdapter).createContainersAndRun(containerDtos, allocatedResourceDtos, test);
        when(testRepository.save(test)).thenReturn(test);

        runAction.execute(context);

        assertEquals(TestState.RUNNING, test.getState());

    }

    //CREATED -> INITIALIZATION -> TEST_PLAN_CREATED -> RESOURCE_ALLOCATED -> BUILD_CONTAINERS -> FAILED_RUN_TEST
    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.statemachine.StateMachineStreamArgs#getArgsFor_runAction")
    void failedRun(Test test,
                         List<ContainerDto> containerDtos,
                         List<AllocatedResourceDto> allocatedResourceDtos) throws Exception {

        when(context.getExtendedState()).thenReturn(extendedState);
        when(extendedState.get("test", Test.class)).thenReturn(test);
        when(extendedState.get("allocatedResources", Object.class)).thenReturn(allocatedResourceDtos);
        when(extendedState.get("containers", Object.class)).thenReturn(containerDtos);
        when(extendedState.get("next", TestState.class)).thenReturn(TestState.FAILED_RUN_TEST);

        doThrow(Exception.class).when(runnerAdapter).createContainersAndRun(containerDtos, allocatedResourceDtos, test);

        runAction.execute(context);

        assertEquals(TestState.FAILED_RUN_TEST, extendedState.get("next", TestState.class));

    }
}
