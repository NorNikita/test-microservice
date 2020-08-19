package ru.pflb.boomq.test.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import ru.pflb.boomq.model.test.ExceptionMessage;
import ru.pflb.boomq.model.test.TestState;
import ru.pflb.boomq.model.test.exception.TestServiceException;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.repository.TestRepository;
import ru.pflb.boomq.test.statemachine.actions.*;
import ru.pflb.boomq.test.statemachine.enums.Events;
import ru.pflb.boomq.test.statemachine.listener.StateMachineListener;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static ru.pflb.boomq.model.test.TestState.*;

/**
 * @link  https://gitlab.boomq.io/boomq/cloud/-/wikis/test-service
 */

@Slf4j
@Configuration
@EnableStateMachineFactory
public class StateMachineConfiguration extends EnumStateMachineConfigurerAdapter<TestState, Events> {

    private static Map<TestState, ExceptionMessage> stateMapException = new HashMap<>();

    private InitializeAction initializeAction;
    private GetTestPlanAction getTestPlanAction;
    private AllocateResourceAction allocateResourceAction;
    private BuildContainersAction createContainersAction;
    private RunAction runAction;
    private TestRepository testRepository;

    public StateMachineConfiguration(InitializeAction initializeAction,
                                     GetTestPlanAction getTestPlanAction,
                                     AllocateResourceAction allocateResourceAction,
                                     BuildContainersAction createContainersAction,
                                     RunAction runAction,
                                     TestRepository testRepository) {
        this.initializeAction = initializeAction;
        this.getTestPlanAction = getTestPlanAction;
        this.allocateResourceAction = allocateResourceAction;
        this.createContainersAction = createContainersAction;
        this.runAction = runAction;
        this.testRepository = testRepository;
    }

    static {
        stateMapException.put(FAILED, ExceptionMessage.FAILED);
        stateMapException.put(MANY_TEST_RUN, ExceptionMessage.MANY_TEST_RUN);
        stateMapException.put(FAIL_GET_TESTPLAN, ExceptionMessage.FAILED_GET_TEST_PLAN);
        stateMapException.put(WAITING_RESOURCES, ExceptionMessage.WAITING_RESOURCES);
        stateMapException.put(ERROR_RESOURCES_ALLOCATED, ExceptionMessage.ERROR_RESOURCES_ALLOCATED);
        stateMapException.put(FAILED_BUILD_CONTAINERS, ExceptionMessage.FAILED_BUILD_CONTAINERS);
        stateMapException.put(FAILED_RUN_TEST, ExceptionMessage.FAILED_RUN_TEST);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<TestState, Events> config) throws Exception {
        config
                .withConfiguration()
                .listener(new StateMachineListener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<TestState, Events> states) throws Exception {
        states
                .withStates()
                .initial(CREATED)

                .choice(AFTER_CREATED)
                .state(FAILED, context
                        -> lastAction(context, FAILED))
                .state(MANY_TEST_RUN, context
                        -> lastAction(context, MANY_TEST_RUN))

                .choice(AFTER_INITIALIZE)
                .state(FAIL_GET_TESTPLAN, context
                        -> lastAction(context, FAIL_GET_TESTPLAN))

                .choice(AFTER_TEST_PLAN_CREATED)
                .state(WAITING_RESOURCES, context
                        -> lastAction(context, WAITING_RESOURCES))
                .state(ERROR_RESOURCES_ALLOCATED, context
                        -> lastAction(context, ERROR_RESOURCES_ALLOCATED))

                .choice(AFTER_RESOURCE_ALLOCATE)
                .state(FAILED_BUILD_CONTAINERS, context
                        -> lastAction(context, FAILED_BUILD_CONTAINERS))

                .choice(AFTER_BUILD_CONTAINERS)
                .state(FAILED_RUN_TEST, context
                        -> lastAction(context, FAILED_RUN_TEST))

                .end(RUNNING)
                .states(EnumSet.allOf(TestState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<TestState, Events> transitions) throws Exception {
        transitions
                .withExternal()
                .source(CREATED)
                .target(AFTER_CREATED)
                .action(initializeAction)

                .and()
                .withChoice()
                .source(AFTER_CREATED)
                .first(FAILED, context -> isGuarded(context, FAILED))
                .then(MANY_TEST_RUN, context -> isGuarded(context, MANY_TEST_RUN))
                .last(INITIALIZATION)

                .and()
                .withExternal()
                .source(INITIALIZATION)
                .target(AFTER_INITIALIZE)
                .action(getTestPlanAction)

                .and()
                .withChoice()
                .source(AFTER_INITIALIZE)
                .first(FAIL_GET_TESTPLAN, context -> isGuarded(context, FAIL_GET_TESTPLAN))
                .last(TEST_PLAN_CREATED)

                .and()
                .withExternal()
                .source(TEST_PLAN_CREATED)
                .target(AFTER_TEST_PLAN_CREATED)
                .action(allocateResourceAction)

                .and()
                .withChoice()
                .source(AFTER_TEST_PLAN_CREATED)
                .first(WAITING_RESOURCES, context -> isGuarded(context, WAITING_RESOURCES))
                .then(ERROR_RESOURCES_ALLOCATED, context -> isGuarded(context, ERROR_RESOURCES_ALLOCATED))
                .last(RESOURCES_ALLOCATED)

                .and()
                .withExternal()
                .source(RESOURCES_ALLOCATED)
                .target(AFTER_RESOURCE_ALLOCATE)
                .action(createContainersAction)

                .and()
                .withChoice()
                .source(AFTER_RESOURCE_ALLOCATE)
                .first(FAILED_BUILD_CONTAINERS, context -> isGuarded(context, FAILED_BUILD_CONTAINERS))
                .last(BUILD_CONTAINERS)

                .and()
                .withExternal()
                .source(BUILD_CONTAINERS)
                .target(AFTER_BUILD_CONTAINERS)
                .action(runAction)

                .and()
                .withChoice()
                .source(AFTER_BUILD_CONTAINERS)
                .first(FAILED_RUN_TEST, context -> isGuarded(context, FAILED_RUN_TEST))
                .last(RUNNING);
    }

    private void lastAction(StateContext<TestState, Events> context, TestState state) {
        Test test = context.getExtendedState().get("test", Test.class);
        context.getExtendedState().getVariables().put("exception",
                new TestServiceException(stateMapException.get(state))
        );

        test.setState(state);
        testRepository.save(test);

        context.getStateMachine().stop();
    }

    private boolean isGuarded(StateContext<TestState, Events> context, TestState state) {
        TestState nextState = context.getExtendedState().get("next", TestState.class);
        return nextState == state;
    }
}