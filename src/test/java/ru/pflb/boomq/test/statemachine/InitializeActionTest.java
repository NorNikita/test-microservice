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
import ru.pflb.boomq.model.test.testprofile.TestProfileDto;
import ru.pflb.boomq.test.adapter.IFileAdapterService;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.model.exception.ReceiveTestParametersException;
import ru.pflb.boomq.test.repository.TestRepository;
import ru.pflb.boomq.test.statemachine.actions.InitializeAction;
import ru.pflb.boomq.test.statemachine.enums.Events;
import ru.pflb.boomq.test.utils.Calculator;
import ru.pflb.boomq.test.utils.Validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitializeActionTest {

    @Mock
    private TestRepository testRepository;

    @Mock
    private IFileAdapterService fileAdapterService;

    @Mock(name = "yamlMapper")
    private ObjectMapper yamlMapper;

    @Mock
    private Validator validator;

    @Mock
    private Calculator calculator;

    @Mock
    private StateContext<TestState, Events> context;

    @Mock
    private ExtendedState extendedState;

    @InjectMocks
    private InitializeAction initializeAction;

    //CREATED -> INITIALIZATION
    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.statemachine.StateMachineStreamArgs#getArgsFor_initializeAction")
    void initialize(Test test,
                    String content,
                    TestSettingDto testSettingDto,
                    TestProfileDto testProfileDto) throws Exception {

        when(context.getExtendedState()).thenReturn(extendedState);
        when(extendedState.get("test", Test.class)).thenReturn(test);

        when(fileAdapterService.getFileContentAsString(anyString())).thenReturn(content);
        when(yamlMapper.readValue(content, TestSettingDto.class)).thenReturn(testSettingDto);
        when(yamlMapper.readValue(content, TestProfileDto.class)).thenReturn(testProfileDto);
        when(calculator.calculationTimeDuration(any(TestSettingDto.class))).thenReturn(testSettingDto);

        doNothing().when(validator).checkRunnigTest(anyLong());
        doNothing().when(validator).validateTestProfileDTO(any(TestProfileDto.class));
        when(testRepository.save(test)).thenReturn(test);

        initializeAction.execute(context);

        assertEquals(TestState.INITIALIZATION, test.getState());
    }

    //CREATED -> FAILED
    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.statemachine.StateMachineStreamArgs#getArgsFor_initializeAction")
    void failed(Test test,
                String content,
                TestSettingDto testSettingDto,
                TestProfileDto testProfileDto) {

        when(context.getExtendedState()).thenReturn(extendedState);
        when(extendedState.get("test", Test.class)).thenReturn(test);
        when(extendedState.get("next", TestState.class)).thenReturn(TestState.FAILED);
        when(fileAdapterService.getFileContentAsString(anyString())).thenThrow(ReceiveTestParametersException.class);

        initializeAction.execute(context);

        assertEquals(TestState.FAILED, extendedState.get("next", TestState.class));
    }

    //CREATED -> MANY_TEST_RUN
    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.statemachine.StateMachineStreamArgs#getArgsFor_initializeAction")
    void manyTestRun(Test test,
                     String content,
                     TestSettingDto testSettingDto,
                     TestProfileDto testProfileDto) {

        when(context.getExtendedState()).thenReturn(extendedState);
        when(extendedState.get("test", Test.class)).thenReturn(test);
        when(extendedState.get("next", TestState.class)).thenReturn(TestState.MANY_TEST_RUN);
        when(fileAdapterService.getFileContentAsString(anyString())).thenReturn(content);
        doThrow(TestServiceException.class).when(validator).checkRunnigTest(anyLong());

        initializeAction.execute(context);

        assertEquals(TestState.MANY_TEST_RUN, extendedState.get("next", TestState.class));
    }
}