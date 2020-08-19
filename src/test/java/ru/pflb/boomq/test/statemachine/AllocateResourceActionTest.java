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
import ru.pflb.boomq.model.test.exception.TestServiceException;
import ru.pflb.boomq.model.testrunner.AllocatedResourceDto;
import ru.pflb.boomq.test.adapter.IResourcesAdapterService;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.repository.TestRepository;
import ru.pflb.boomq.test.statemachine.actions.AllocateResourceAction;
import ru.pflb.boomq.test.statemachine.enums.Events;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AllocateResourceActionTest {

    @Mock
    private IResourcesAdapterService resourcesAdapter;

    @Mock
    private TestRepository testRepository;

    @Mock
    private StateContext<TestState, Events> context;

    @Mock
    private ExtendedState extendedState;

    @InjectMocks
    private AllocateResourceAction allocateResourceAction;

    //CREATED -> INITIALIZATION -> TEST_PLAN_CREATED -> RESOURCE_ALLOCATED
    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.statemachine.StateMachineStreamArgs#getArgsFor_allocateResourceAction")
    void resourceAllocated(Test test,
                           List<AllocatedResourceDto> allocatedResourceDtos) {

        when(context.getExtendedState()).thenReturn(extendedState);
        when(extendedState.get("test", Test.class)).thenReturn(test);

        when(resourcesAdapter.allocateResources(test)).thenReturn(allocatedResourceDtos);
        when(testRepository.save(test)).thenReturn(test);

        allocateResourceAction.execute(context);

        assertEquals(TestState.RESOURCES_ALLOCATED, test.getState());

    }

    //CREATED -> INITIALIZATION -> TEST_PLAN_CREATED -> WAITING_RESOURCES
    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.statemachine.StateMachineStreamArgs#getArgsFor_allocateResourceAction")
    void setAllocateResource(Test test,
                             List<AllocatedResourceDto> allocatedResourceDtos) {

        when(context.getExtendedState()).thenReturn(extendedState);
        when(extendedState.get("test", Test.class)).thenReturn(test);
        when(extendedState.get("next", TestState.class)).thenReturn(TestState.WAITING_RESOURCES);

        when(resourcesAdapter.allocateResources(test)).thenReturn(null);

        allocateResourceAction.execute(context);

        assertEquals(TestState.WAITING_RESOURCES, extendedState.get("next", TestState.class));
    }

    //CREATED -> INITIALIZATION -> TEST_PLAN_CREATED -> ERROR_RESOURCE_ALLOCATED
    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.statemachine.StateMachineStreamArgs#getArgsFor_allocateResourceAction")
    void errorAllocated(Test test,
                        List<AllocatedResourceDto> allocatedResourceDtos) {

        when(context.getExtendedState()).thenReturn(extendedState);
        when(extendedState.get("test", Test.class)).thenReturn(test);
        when(extendedState.get("next", TestState.class)).thenReturn(TestState.ERROR_RESOURCES_ALLOCATED);

        when(resourcesAdapter.allocateResources(test)).thenThrow(TestServiceException.class);

        allocateResourceAction.execute(context);

        assertEquals(TestState.ERROR_RESOURCES_ALLOCATED, extendedState.get("next", TestState.class));
    }
}