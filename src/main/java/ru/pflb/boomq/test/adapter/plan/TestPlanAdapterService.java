package ru.pflb.boomq.test.adapter.plan;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pflb.boomq.model.projectservice.dto.testproject.Group;
import ru.pflb.boomq.model.projectservice.dto.testproject.parameters.Parameter;
import ru.pflb.boomq.model.test.ExceptionMessage;
import ru.pflb.boomq.model.test.exception.TestServiceException;
import ru.pflb.boomq.model.test.settings.TestSettingDto;
import ru.pflb.boomq.model.test.testprofile.TestProfileDto;
import ru.pflb.boomq.model.testplan.Scenario;
import ru.pflb.boomq.model.testplan.Step;
import ru.pflb.boomq.model.testplan.TestPlanReferencePrivateDto;
import ru.pflb.boomq.test.adapter.ITestPlanAdapterService;
import ru.pflb.boomq.test.iface.TestPlanClient;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.model.entity.TestPlan;
import ru.pflb.boomq.test.utils.Builder;
import ru.pflb.boomq.test.utils.Calculator;

import java.util.List;

@Slf4j
@Service
public class TestPlanAdapterService implements ITestPlanAdapterService {

    private Builder builder;
    private Calculator calculator;
    private ObjectMapper objectMapper;
    private TestPlanClient testPlanClient;

    public TestPlanAdapterService(Builder builder,
                                  Calculator calculator,
                                  ObjectMapper objectMapper,
                                  TestPlanClient testPlanClient) {
        this.builder = builder;
        this.calculator = calculator;
        this.objectMapper = objectMapper;
        this.testPlanClient = testPlanClient;
    }

    @Override
    public TestPlan createTestPlan(Test test) throws Exception {
        log.info("try get test plan from test-plan-service. testId {}", test.getTestId());

        TestProfileDto testProfileDto = objectMapper.readValue(test.getTestProfile(), TestProfileDto.class);
        TestSettingDto testSettingDto = objectMapper.readValue(test.getSettings(), TestSettingDto.class);

        List<Step> steps = calculator.calculateStep(testSettingDto);
        List<Group> groups = testProfileDto.getGroups();
        List<Parameter> parameters = testSettingDto.getParameters();

        Scenario scenario = Scenario.builder()
                .steps(steps)
                .groups(groups)
                .parameters(parameters)
                .build();

        Long userId = test.getUserId();
        Long testProjectId = test.getProjectId();

        TestPlanReferencePrivateDto testPlanDto;
        try {
            testPlanDto = testPlanClient.createTestPlan(userId, testProjectId, scenario);
            log.info("success request to test-plan-service for getting test plan! testId {}", test.getTestId());

        } catch (FeignException e) {

            log.error("failed request to test-plan service for getting test plan! testId {}, error message {}",
                    test.getTestId(), e.getMessage());
            throw new TestServiceException(ExceptionMessage.FAILED_GET_TEST_PLAN);
        }

        return builder.buildTestPlan(testPlanDto);
    }

}
