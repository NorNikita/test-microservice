package ru.pflb.boomq.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.pflb.boomq.model.test.Action;
import ru.pflb.boomq.model.test.TestDto;
import ru.pflb.boomq.model.test.TestEventDto;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.service.ITestService;

import javax.validation.Valid;

@Slf4j
@RestController
public class TestController {

    private ITestService testService;

    public TestController(ITestService testService) {
        this.testService = testService;
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Long> startTest(final @AuthenticationPrincipal Jwt jwt,
                                             @RequestBody @Valid TestEventDto testEventDto) {
        log.info("try start test from project with id = {}", testEventDto.getProjectId());

        Test test = testService.createTest(jwt, testEventDto);
        testService.runTest(test);

        log.info("test accepted to run! testId {}", test.getTestId());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(test.getTestId());
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @PutMapping("/{testId}")
    public ResponseEntity<TestDto> updateTestStatus(@AuthenticationPrincipal Jwt jwt,
                                                    @RequestParam Action action,
                                                    @PathVariable Long testId) {
        log.info("try update status test with id = {}. action: {}", testId, action);

        TestDto testDto = testService.updateTestStatus(jwt, testId, action);

        log.info("status test id = {} has been updated! action: {}", testId, action);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(testDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<TestDto>> getAllUserTests(final @AuthenticationPrincipal Jwt jwt, Pageable pageable) {
        Long userId = (Long) jwt.getClaims().get("user_id");
        log.info("try get all test of user with id = {}", userId);

        Page<TestDto> page = testService.getUserTests(userId, pageable);

        log.info("get all pageable test of user with id = {}. size content: {}", userId, page.getContent());
        return ResponseEntity.ok(page);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/{testId}")
    public ResponseEntity<TestDto> getTest(final @AuthenticationPrincipal Jwt jwt, @PathVariable Long testId) {
        log.info("try get test with id = {}", testId);

        TestDto testDto = testService.getTest(jwt, testId);

        log.info("get test with id = {}. testDto: {}", testId, testDto);
        return ResponseEntity.ok(testDto);
    }
}