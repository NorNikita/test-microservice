package ru.pflb.boomq.test.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.jwt.Jwt;
import ru.pflb.boomq.model.test.Action;
import ru.pflb.boomq.model.test.TestDto;
import ru.pflb.boomq.model.test.TestEventDto;
import ru.pflb.boomq.model.test.TestState;
import ru.pflb.boomq.model.test.exception.TestServiceException;
import ru.pflb.boomq.test.adapter.ITestRunnerAdapterService;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.repository.TestRepository;
import ru.pflb.boomq.test.service.impl.TestServiceImpl;
import ru.pflb.boomq.test.utils.Builder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestServiceTest {

    @Mock
    private TestRepository testRepository;

    @Mock
    private ITestRunnerAdapterService testRunnerAdapterService;

    @Mock
    private Builder builder;

    @InjectMocks
    private TestServiceImpl testService;

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.service.TestServiceStreamArgs#getArgsFor_createTest")
    void createTest(Jwt jwt,
                    Test test,
                    TestEventDto testEventDto) {

        when(builder.buildTest(testEventDto)).thenReturn(test);
        when(testRepository.save(test)).thenReturn(test);

        Test createdTest = testService.createTest(jwt, testEventDto);

        assertEquals(test.getTestId(), createdTest.getTestId());
        assertEquals(TestState.CREATED, createdTest.getState());
    }

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.service.TestServiceStreamArgs#getArgsFor_updateTest")
    void stopTest(Jwt jwt,
                  Test test,
                  TestDto testDTO) {
        when(testRepository.findById(anyLong())).thenReturn(Optional.of(test));
        when(testRepository.save(any(Test.class))).thenReturn(test);
        doNothing().when(testRunnerAdapterService).stopTestContainer(test);
        when(builder.buildTestDto(any(Test.class))).thenReturn(testDTO);

        TestDto testDto = testService.updateTestStatus(jwt, test.getTestId(), Action.STOP);
        assertEquals(TestState.STOPPED, testDto.getState());
    }

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.service.TestServiceStreamArgs#getArgsFor_updateTest")
    void updateUnknownTest(Jwt jwt,
                           Test test,
                           TestDto testDTO) {
        //test not found
        when(testRepository.findById(test.getTestId())).thenThrow(TestServiceException.class);
        assertThrows(TestServiceException.class, () -> testService.updateTestStatus(jwt, test.getTestId(), Action.STOP));
    }

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.service.TestServiceStreamArgs#getArgsFor_updateTest")
    void updateTestOfAnotherUser(Jwt jwt,
                           Test test,
                           TestDto testDTO) {
        //access denied
        test.setUserId(100L);
        when(testRepository.findById(test.getTestId())).thenReturn(Optional.of(test));
        assertThrows(TestServiceException.class, () -> testService.updateTestStatus(jwt, test.getTestId(), Action.STOP));
    }

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.service.TestServiceStreamArgs#getArgsFor_updateTest")
    void failedStopTest(Jwt jwt,
                        Test test,
                        TestDto testDTO) {
        when(testRepository.findById(anyLong())).thenReturn(Optional.of(test));
        doThrow(TestServiceException.class).when(testRunnerAdapterService).stopTestContainer(test);

        assertThrows(TestServiceException.class, () -> testService.updateTestStatus(jwt, test.getTestId(), Action.STOP));
    }

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.service.TestServiceStreamArgs#getArgsFor_getTest")
    void getTest(Jwt jwt,
                 Test test,
                 TestDto testDto) {
        when(testRepository.findById(anyLong())).thenReturn(Optional.of(test));
        when(builder.buildTestDto(test)).thenReturn(testDto);

        TestDto getTest = testService.getTest(jwt, test.getTestId());
        assertEquals(test.getState(), getTest.getState());

        when(testRepository.findById(anyLong())).thenThrow(TestServiceException.class);
        assertThrows(TestServiceException.class, () -> testService.getTest(jwt, test.getTestId()));
    }

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.service.TestServiceStreamArgs#getArgsFor_getTest")
    void getTestAnotherUser(Jwt jwt,
                            Test test,
                            TestDto testDto) {
        test.setUserId(100L);
        when(testRepository.findById(anyLong())).thenReturn(Optional.of(test));

        assertThrows(TestServiceException.class, () -> testService.getTest(jwt, test.getTestId()));
    }

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.service.TestServiceStreamArgs#getArgsFor_getUserTests")
    void getUserTest(Long userId,
                     Pageable pageable,
                     Page<Test> page,
                     Page<TestDto> dtoPage) {
        when(testRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        page.stream().map(test -> when(builder.buildTestDto(test)).thenReturn(dtoPage.getContent().get(0)));

        Page<TestDto> userTests = testService.getUserTests(userId, pageable);

        assertEquals(page.getContent().size(), userTests.getContent().size());
    }

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.service.TestServiceStreamArgs#getLastServerTime")
    void getLastServerTime(Optional<Test> queryResult, Instant expectedLastServerTime){
        when(testRepository.findFirstByOrderByFromDateDesc()).thenReturn(queryResult);
        Instant lastServerTime = testService.getLastServerTime();
        assertEquals(expectedLastServerTime, lastServerTime);
    }

}