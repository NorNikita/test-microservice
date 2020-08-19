package ru.pflb.boomq.test.args.provider;

import org.springframework.statemachine.ExtendedState;

import java.util.Map;

public class ExtendedStateProvider {

    public static ExtendedState getExtendedState() {
        return new ExtendedState() {
            @Override
            public Map<Object, Object> getVariables() {
                return null;
            }

            @Override
            public <T> T get(Object key, Class<T> type) {
                return null;
            }

            @Override
            public void setExtendedStateChangeListener(ExtendedStateChangeListener listener) {

            }
        };
    }
}
