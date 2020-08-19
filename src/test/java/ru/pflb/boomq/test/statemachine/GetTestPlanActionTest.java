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
import ru.pflb.boomq.test.adapter.ITestPlanAdapterService;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.model.entity.TestPlan;
import ru.pflb.boomq.test.repository.TestPlanRepository;
import ru.pflb.boomq.test.repository.TestRepository;
import ru.pflb.boomq.test.statemachine.actions.GetTestPlanAction;
import ru.pflb.boomq.test.statemachine.enums.Events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTestPlanActionTest {

    @Mock
    private ITestPlanAdapterService planAdapter;

    @Mock
    private TestRepository testRepository;

    @Mock
    private TestPlanRepository testPlanRepository;

    @Mock
    private StateContext<TestState, Events> context;

    @Mock
    private ExtendedState extendedState;

    @InjectMocks
    private GetTestPlanAction getTestPlanAction;

    //CREATED -> INITIALIZATION -> TEST_PLAN_CREATED
    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.statemachine.StateMachineStreamArgs#getArgsFor_getTestPlanAction")
    void testPlanCreated(Test test,
                         TestPlan testPlan) throws Exception {
        when(context.getExtendedState()).thenReturn(extendedState);
        when(extendedState.get("test", Test.class)).thenReturn(test);

        when(planAdapter.createTestPlan(test)).thenReturn(testPlan);
        when(testPlanRepository.save(testPlan)).thenReturn(testPlan);
        when(testRepository.save(test)).thenReturn(test);

        getTestPlanAction.execute(context);

        assertEquals(TestState.TEST_PLAN_CREATED, test.getState());
    }

    //CREATED -> INITIALIZATION -> FAILED_GET_TESTPLAN
    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.statemachine.StateMachineStreamArgs#getArgsFor_getTestPlanAction")
    void failedGetTestPlan(Test test,
                         TestPlan testPlan) throws Exception {
        when(context.getExtendedState()).thenReturn(extendedState);
        when(extendedState.get("test", Test.class)).thenReturn(test);
        when(extendedState.get("next", TestState.class)).thenReturn(TestState.FAIL_GET_TESTPLAN);
        when(planAdapter.createTestPlan(test)).thenThrow(Exception.class);

        getTestPlanAction.execute(context);
        assertEquals(TestState.FAIL_GET_TESTPLAN, extendedState.get("next", TestState.class));
    }


}
