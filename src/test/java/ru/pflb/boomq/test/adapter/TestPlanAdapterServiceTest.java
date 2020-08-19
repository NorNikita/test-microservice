package ru.pflb.boomq.test.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pflb.boomq.model.test.exception.TestServiceException;
import ru.pflb.boomq.model.test.settings.TestSettingDto;
import ru.pflb.boomq.model.test.testprofile.TestProfileDto;
import ru.pflb.boomq.model.testplan.Scenario;
import ru.pflb.boomq.model.testplan.Step;
import ru.pflb.boomq.model.testplan.TestPlanReferencePrivateDto;
import ru.pflb.boomq.test.adapter.plan.TestPlanAdapterService;
import ru.pflb.boomq.test.iface.TestPlanClient;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.model.entity.TestPlan;
import ru.pflb.boomq.test.utils.Builder;
import ru.pflb.boomq.test.utils.Calculator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestPlanAdapterServiceTest {

    @Mock
    private Calculator calculator;

    @Mock
    private Builder builder;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TestPlanClient testPlanClient;

    @InjectMocks
    private TestPlanAdapterService testPlanAdapterService;

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.adapter.TestPlanAdapterServiceArgs#getArgsFor_createTestPlan")
    void createTestPlan(Test test,
                        TestSettingDto testSettingDto,
                        TestProfileDto testProfileDto,
                        TestPlanReferencePrivateDto testPlanReferencePrivateDto,
                        List<Step> stepList,
                        TestPlan testPlan,
                        Scenario scenario) throws Exception {

        when(objectMapper.readValue(test.getTestProfile(), TestProfileDto.class)).thenReturn(testProfileDto);
        when(objectMapper.readValue(test.getSettings(), TestSettingDto.class)).thenReturn(testSettingDto);
        when(calculator.calculateStep(testSettingDto)).thenReturn(stepList);
        when(testPlanClient.createTestPlan(test.getUserId(), test.getProjectId(), scenario)).thenReturn(testPlanReferencePrivateDto);
        when(builder.buildTestPlan(testPlanReferencePrivateDto)).thenReturn(testPlan);

        TestPlan testPlanService = testPlanAdapterService.createTestPlan(test);
        assertEquals(testPlan.getFileExtension(), testPlanService.getFileExtension());
        assertEquals(testPlan.getJmxUri(), testPlanService.getJmxUri());

        when(testPlanClient.createTestPlan(test.getUserId(), test.getProjectId(), scenario)).thenThrow(FeignException.class);
        assertThrows(TestServiceException.class, () -> testPlanAdapterService.createTestPlan(test));
    }
}
