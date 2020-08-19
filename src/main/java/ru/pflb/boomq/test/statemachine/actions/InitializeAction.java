package ru.pflb.boomq.test.statemachine.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import ru.pflb.boomq.model.test.TestState;
import ru.pflb.boomq.model.test.exception.TestServiceException;
import ru.pflb.boomq.model.test.settings.TestSettingDto;
import ru.pflb.boomq.model.test.testprofile.TestProfileDto;
import ru.pflb.boomq.test.adapter.IFileAdapterService;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.model.exception.ReceiveTestParametersException;
import ru.pflb.boomq.test.repository.TestRepository;
import ru.pflb.boomq.test.statemachine.enums.Events;
import ru.pflb.boomq.test.utils.Calculator;
import ru.pflb.boomq.test.utils.Validator;

import java.io.IOException;

@Slf4j
@Component
public class InitializeAction implements Action<TestState, Events>{

    private TestRepository testRepository;
    private IFileAdapterService fileAdapterService;
    private ObjectMapper yamlMapper;
    private ObjectMapper jsonMapper = new ObjectMapper();
    private Validator validator;
    private Calculator calculator;

    public InitializeAction(TestRepository testRepository,
                            IFileAdapterService fileAdapterService,
                            @Qualifier("yamlObjectMapper") ObjectMapper yamlMapper,
                            Validator validator,
                            Calculator calculator) {
        this.testRepository = testRepository;
        this.fileAdapterService = fileAdapterService;
        this.yamlMapper = yamlMapper;
        this.validator = validator;
        this.calculator = calculator;
    }

    @Override
    public void execute(StateContext<TestState, Events> context) {
        Test test = context.getExtendedState().get("test", Test.class);

        try {
            log.info("try initialize test with id = {}", test.getTestId());

            String content = fileAdapterService.getFileContentAsString(test.getBucketUri());

            TestSettingDto testSettingDto = yamlMapper.readValue(content, TestSettingDto.class);

            TestProfileDto testProfileDto = yamlMapper.readValue(content, TestProfileDto.class);

            testSettingDto = calculator.calculationTimeDuration(testSettingDto);

            validator.validateTestProfileDTO(testProfileDto);

            validator.checkRunnigTest(test.getProjectId());

            test.setSettings(jsonMapper.writeValueAsString(testSettingDto));
            test.setTestProfile(jsonMapper.writeValueAsString(testProfileDto));
            test.setState(TestState.INITIALIZATION);
            test.setType(testSettingDto.getTestType());
            test.setCountUsers((long) testSettingDto.getUsersPerStep() * testSettingDto.getStepCount());
            test.setTotalDuration(testSettingDto.getTotalDuration());
            testRepository.save(test);

            context.getExtendedState().getVariables().put("test", test);
            log.info("test with id = {} initialized!", test.getTestId());
        } catch (ReceiveTestParametersException | IOException e) {

            context.getExtendedState().getVariables().put("next", TestState.FAILED);
            log.error("failed error start test with id = {}", test.getTestId());

        } catch (TestServiceException exc) {

            context.getExtendedState().getVariables().put("next", TestState.MANY_TEST_RUN);
            log.error(" many test run! error start test with id = {}", test.getTestId());
        }
    }
}