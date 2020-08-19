package ru.pflb.boomq.test.statemachine.actions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import ru.pflb.boomq.model.test.TestState;
import ru.pflb.boomq.test.adapter.ITestPlanAdapterService;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.model.entity.TestPlan;
import ru.pflb.boomq.test.repository.TestPlanRepository;
import ru.pflb.boomq.test.repository.TestRepository;
import ru.pflb.boomq.test.statemachine.enums.Events;

@Slf4j
@Component
public class GetTestPlanAction implements Action<TestState, Events> {

    private ITestPlanAdapterService planAdapter;
    private TestRepository testRepository;
    private TestPlanRepository testPlanRepository;

    public GetTestPlanAction(ITestPlanAdapterService planAdapter,
                             TestRepository testRepository,
                             TestPlanRepository testPlanRepository) {
        this.planAdapter = planAdapter;
        this.testRepository = testRepository;
        this.testPlanRepository = testPlanRepository;
    }

    @Override
    public void execute(StateContext<TestState, Events> context) {
        Test test = context.getExtendedState().get("test", Test.class);

        try {
            log.info("try get test plan for test with id = {}", test.getTestId());

            TestPlan testPlan = planAdapter.createTestPlan(test);

            testPlan.setTestId(test.getTestId());
            testPlanRepository.save(testPlan);

            test.setState(TestState.TEST_PLAN_CREATED);
            testRepository.save(test);

            context.getExtendedState().getVariables().put("test", test);
            log.info("test plan created for test with id = {}", test.getTestId());

        } catch(Exception e) {
            context.getExtendedState().getVariables().put("next", TestState.FAIL_GET_TESTPLAN);
            log.error("can not get test plan for test with id = {}", test.getTestId());
        }
    }
}