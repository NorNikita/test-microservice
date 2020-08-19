package ru.pflb.boomq.test.statemachine;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.pflb.boomq.model.test.settings.TestSettingDto;
import ru.pflb.boomq.model.testrunner.AllocatedResourceDto;
import ru.pflb.boomq.model.testrunner.ContainerDto;
import ru.pflb.boomq.test.adapter.IContainerAdapterService;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.model.entity.TestPlan;
import ru.pflb.boomq.test.repository.TestPlanRepository;
import ru.pflb.boomq.test.repository.TestRepository;
import ru.pflb.boomq.test.statemachine.actions.BuildContainersAction;
import ru.pflb.boomq.test.statemachine.enums.Events;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuildContainersActionTest {

    @Mock
    private IContainerAdapterService containerAdapter;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TestRepository testRepository;

    @Mock
    private TestPlanRepository testPlanRepository;

    @Mock
    private StateContext<TestState, Events> context;

    @Mock
    private ExtendedState extendedState;

    @InjectMocks
    private BuildContainersAction buildContainersAction;

    //CREATED -> INITIALIZATION -> TEST_PLAN_CREATED -> RESOURCE_ALLOCATED -> BUILD_CONTAINERS
    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.statemachine.StateMachineStreamArgs#getArgsFor_buildContainersAction")
    void buildContainers(Test test,
                         List<AllocatedResourceDto> allocatedResourceDtos,
                         TestPlan testPlan,
                         TestSettingDto testSettingDto,
                         List<ContainerDto> containerDtos) throws IOException {

        when(context.getExtendedState()).thenReturn(extendedState);
        when(extendedState.get("test", Test.class)).thenReturn(test);
        when(extendedState.get("allocatedResources", Object.class)).thenReturn(allocatedResourceDtos);

        when(testPlanRepository.findByTestId(test.getTestId())).thenReturn(testPlan);
        when(objectMapper.readValue(test.getSettings(), TestSettingDto.class)).thenReturn(testSettingDto);
        when(containerAdapter.buildContainers(allocatedResourceDtos, test, testPlan, testSettingDto)).thenReturn(containerDtos);
        when(testRepository.save(test)).thenReturn(test);

        buildContainersAction.execute(context);

        assertEquals(TestState.BUILD_CONTAINERS, test.getState());
    }

    //CREATED -> INITIALIZATION -> TEST_PLAN_CREATED -> RESOURCE_ALLOCATED -> FAILED_BUILD_CONTAINERS
    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.statemachine.StateMachineStreamArgs#getArgsFor_buildContainersAction")
    void failedBuildContainers(Test test,
                               List<AllocatedResourceDto> allocatedResourceDtos,
                               TestPlan testPlan,
                               TestSettingDto testSettingDto,
                               List<ContainerDto> containerDtos) throws IOException {

        when(context.getExtendedState()).thenReturn(extendedState);
        when(extendedState.get("test", Test.class)).thenReturn(test);
        when(extendedState.get("allocatedResources", Object.class)).thenReturn(allocatedResourceDtos);
        when(extendedState.get("next", TestState.class)).thenReturn(TestState.FAILED_BUILD_CONTAINERS);

        when(testPlanRepository.findByTestId(test.getTestId())).thenReturn(testPlan);
        when(objectMapper.readValue(test.getSettings(), TestSettingDto.class)).thenReturn(testSettingDto);
        when(containerAdapter.buildContainers(allocatedResourceDtos, test, testPlan, testSettingDto)).thenThrow(TestServiceException.class);

        buildContainersAction.execute(context);

        assertEquals(TestState.FAILED_BUILD_CONTAINERS, extendedState.get("next", TestState.class));
    }
}
