package ru.pflb.boomq.test.statemachine.actions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import ru.pflb.boomq.model.test.TestState;
import ru.pflb.boomq.model.test.exception.TestServiceException;
import ru.pflb.boomq.model.testrunner.AllocatedResourceDto;
import ru.pflb.boomq.test.adapter.IResourcesAdapterService;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.repository.TestRepository;
import ru.pflb.boomq.test.statemachine.enums.Events;

import java.util.List;

@Slf4j
@Component
public class AllocateResourceAction implements Action<TestState, Events> {

    private IResourcesAdapterService resourcesAdapter;
    private TestRepository testRepository;

    public AllocateResourceAction(IResourcesAdapterService resourcesAdapter, TestRepository testRepository) {
        this.resourcesAdapter = resourcesAdapter;
        this.testRepository = testRepository;
    }

    @Override
    public void execute(StateContext<TestState, Events> context) {
        Test test = context.getExtendedState().get("test", Test.class);

        try {
            log.info("try allocate resources for test with id = {}", test.getTestId());

            List<AllocatedResourceDto> allocateResources = resourcesAdapter.allocateResources(test);

            if (allocateResources == null) {
                context.getExtendedState().getVariables().put("next", TestState.WAITING_RESOURCES);
                log.info("waiting resources for test with id = {} ...", test.getTestId());

            } else {
                test.setState(TestState.RESOURCES_ALLOCATED);
                testRepository.save(test);

                context.getExtendedState().getVariables().put("allocatedResources", allocateResources);
                context.getExtendedState().getVariables().put("test", test);
                log.info("resources for test with id = {} successful allocated!", test.getTestId());
            }

        } catch (TestServiceException e) {
            context.getExtendedState().getVariables().put("next", TestState.ERROR_RESOURCES_ALLOCATED);
            log.error("fail in process allocated resources! test with id = {}, exception message: {}", test.getTestId(), e.getExceptionMessage());
        }
    }
}
