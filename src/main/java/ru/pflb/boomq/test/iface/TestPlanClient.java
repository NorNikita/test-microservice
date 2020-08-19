package ru.pflb.boomq.test.iface;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.pflb.boomq.model.testplan.Scenario;
import ru.pflb.boomq.model.testplan.TestPlanReferencePrivateDto;
import ru.pflb.boomq.test.iface.configurations.TestPlanControllerConfig;

@FeignClient(name = "Test-Plan-Generator-Service", url = "${boomq.test-plan.host}", configuration = TestPlanControllerConfig.class)
public interface TestPlanClient {

    @PostMapping(value = "/v2/tests/new")
    TestPlanReferencePrivateDto createTestPlan(@RequestParam(name = "userId") Long userId,
                                               @RequestParam(name = "testProjectId") Long testProjectId,
                                               @RequestBody Scenario scenario);
}
