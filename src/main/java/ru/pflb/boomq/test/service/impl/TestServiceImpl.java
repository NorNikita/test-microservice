package ru.pflb.boomq.test.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;
import ru.pflb.boomq.model.test.Action;
import ru.pflb.boomq.model.test.ExceptionMessage;
import ru.pflb.boomq.model.test.TestDto;
import ru.pflb.boomq.model.test.TestEventDto;
import ru.pflb.boomq.model.test.TestState;
import ru.pflb.boomq.model.test.exception.TestServiceException;
import ru.pflb.boomq.test.adapter.ITestRunnerAdapterService;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.model.entity.Test_;
import ru.pflb.boomq.test.repository.TestRepository;
import ru.pflb.boomq.test.service.ITestService;
import ru.pflb.boomq.test.statemachine.enums.Events;
import ru.pflb.boomq.test.utils.Builder;

import java.time.Instant;
import java.time.ZoneOffset;

@Slf4j
@Service
public class TestServiceImpl implements ITestService {

    private StateMachineFactory<TestState, Events> factory;
    private ITestRunnerAdapterService runnerAdapter;
    private TestRepository testRepository;
    private Builder builder;

    public TestServiceImpl(StateMachineFactory<TestState, Events> factory,
                           ITestRunnerAdapterService runnerAdapter,
                           TestRepository testRepository,
                           Builder builder) {
        this.factory = factory;
        this.runnerAdapter = runnerAdapter;
        this.testRepository = testRepository;
        this.builder = builder;
    }

    @Override
    public Test createTest(Jwt jwt, TestEventDto testEventDto) {
        log.info("try created test from project with id: {}", testEventDto.getProjectId());

        String userName = (String) jwt.getClaims().get("user_name");
        Long userId = (Long) jwt.getClaims().get("user_id");

        Test test = builder.buildTest(testEventDto);
        test.setUserId(userId);
        test.setUserName(userName);

        testRepository.save(test);

        log.info("test with id = {} created!", test.getTestId());
        return test;
    }

    @Async
    @Override
    public void runTest(Test test) {

        StateMachine<TestState, Events> stateMachine = factory.getStateMachine();

        ExtendedState extendedState = stateMachine.getExtendedState();
        extendedState.getVariables().put("test", test);

        stateMachine.start();

        TestServiceException exception = extendedState.get("exception", TestServiceException.class);

        if (exception != null) {
            throw exception;
        }
    }

    @Override
    public TestDto updateTestStatus(Jwt jwt, Long testId, Action action) {
        Test test = testRepository.findById(testId).orElseThrow(() -> new TestServiceException(ExceptionMessage.TEST_NOT_FOUND));
        Long userId = (Long) jwt.getClaims().get("user_id");

        if (test.getUserId().compareTo(userId) != 0) {
            throw new TestServiceException(ExceptionMessage.ACCESS_DENIED);
        }

        switch (action) {
            case STOP: {
                try {
                    runnerAdapter.stopTestContainer(test);
                    test.setState(TestState.STOPPED);
                    testRepository.save(test);

                } catch (Exception e) {
                    log.error("failed stop test with id = {}, message: {}", testId, e.getMessage());
                    throw new TestServiceException(ExceptionMessage.FAILED_STOP_TEST);
                }
                break;
            }
            default: {
                log.error("unknown action! testId = {}, action: {}", testId, action);
                throw new TestServiceException(ExceptionMessage.UNKNOWN_ACTION);
            }
        }

        return builder.buildTestDto(test);
    }

    @Override
    public TestDto getTest(Jwt jwt, Long testId) {
        Test test = testRepository.findById(testId).orElseThrow(() -> new TestServiceException(ExceptionMessage.TEST_NOT_FOUND));
        Long userId = (Long) jwt.getClaims().get("user_id");

        if (userId.compareTo(test.getUserId()) != 0) {
            throw new TestServiceException(ExceptionMessage.ACCESS_DENIED);
        }

        return builder.buildTestDto(test);
    }

    @Override
    public Page<TestDto> getUserTests(Long userId, Pageable pageable) {

        Specification<Test> specification = (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Test_.userId), userId);

        return testRepository.findAll(specification, pageable).map(test -> builder.buildTestDto(test));
    }

    @Override
    public Instant getLastServerTime() {
        return testRepository.findFirstByOrderByFromDateDesc()
                .map(Test::getFromDate)
                .map(fromTime -> fromTime.toInstant(ZoneOffset.UTC))
                .orElseGet(() -> Instant.ofEpochSecond(0));
    }
}