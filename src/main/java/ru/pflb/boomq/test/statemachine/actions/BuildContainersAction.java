package ru.pflb.boomq.test.statemachine.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
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
import ru.pflb.boomq.test.statemachine.enums.Events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
public class BuildContainersAction implements Action<TestState, Events> {

    private IContainerAdapterService containerAdapter;
    private ObjectMapper objectMapper;
    private TestRepository testRepository;
    private TestPlanRepository testPlanRepository;

    public BuildContainersAction(IContainerAdapterService containerAdapter,
                                 ObjectMapper objectMapper,
                                 TestRepository testRepository,
                                 TestPlanRepository testPlanRepository) {
        this.containerAdapter = containerAdapter;
        this.objectMapper = objectMapper;
        this.testRepository = testRepository;
        this.testPlanRepository = testPlanRepository;
    }

    @Override
    public void execute(StateContext<TestState, Events> context) {
        Test test = context.getExtendedState().get("test", Test.class);

        Object resources = context.getExtendedState().get("allocatedResources", Object.class);
        List<AllocatedResourceDto> allocateResources = new ArrayList<>((Collection<AllocatedResourceDto>) resources);

        try {
            log.info("try build containers for test with id = {}", test.getTestId());

            TestPlan testPlan = testPlanRepository.findByTestId(test.getTestId());

            TestSettingDto testSettingDto = objectMapper.readValue(test.getSettings(), TestSettingDto.class);

            List<ContainerDto> containers = containerAdapter.buildContainers(allocateResources, test, testPlan, testSettingDto);

            test.setState(TestState.BUILD_CONTAINERS);
            testRepository.save(test);

            context.getExtendedState().getVariables().put("containers", containers);
            log.info("containers for test with id = {} successful created!", test.getTestId());

        } catch (TestServiceException | JsonProcessingException e) {
            context.getExtendedState().getVariables().put("next", TestState.FAILED_BUILD_CONTAINERS);
            log.error("create container for test with id = {} failed!", test.getTestId());
        }
    }
}
