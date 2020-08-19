package ru.pflb.boomq.test.statemachine.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import ru.pflb.boomq.model.test.TestState;
import ru.pflb.boomq.test.statemachine.enums.Events;

import java.util.Optional;

public class StateMachineListener extends StateMachineListenerAdapter<TestState, Events> {

    private Logger log = LoggerFactory.getLogger(StateMachineListener.class);

    @Override
    public void transition(Transition<TestState, Events> transition) {
        log.info("transition from source: {} to target: {}\n",
                ofNullable(transition.getSource()),
                ofNullable(transition.getTarget()));
    }

    private Object ofNullable(State state) {
        return Optional.ofNullable(state)
                .map(State::getId)
                .orElse(null);
    }
}