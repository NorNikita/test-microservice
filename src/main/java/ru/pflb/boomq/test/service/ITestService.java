package ru.pflb.boomq.test.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import ru.pflb.boomq.licensing.service.ServerTimeSource;
import ru.pflb.boomq.model.test.Action;
import ru.pflb.boomq.model.test.TestDto;
import ru.pflb.boomq.model.test.TestEventDto;
import ru.pflb.boomq.test.model.entity.Test;

public interface ITestService extends ServerTimeSource {

    Test createTest(Jwt jwt, TestEventDto testEventDto);

    void runTest(Test test);

    TestDto updateTestStatus(Jwt jwt, Long testId, Action action);

    TestDto getTest(Jwt jwt, Long testId);

    Page<TestDto> getUserTests(Long userId, Pageable pageable);
}
