package ru.pflb.boomq.test.utils;

import org.springframework.stereotype.Component;
import ru.pflb.boomq.model.test.TestDto;
import ru.pflb.boomq.model.test.TestEventDto;
import ru.pflb.boomq.model.testplan.TestPlanReferencePrivateDto;
import ru.pflb.boomq.model.testrunner.ResourceDto;
import ru.pflb.boomq.test.model.entity.Resource;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.model.entity.TestPlan;

@Component
public class Builder {

    public Test buildTest(TestEventDto dto) {
        return Test.builder()
                .projectId(dto.getProjectId())
                .bucketUri(dto.getBucketUri())
                .state(dto.getState())
                .type(dto.getType())
                .comment(dto.getComment())
                .version(dto.getVersion())
                .build();
    }

    public TestDto buildTestDto(Test test) {
        return TestDto.builder()
                .testId(test.getTestId())
                .projectId(test.getProjectId())
                .userId(test.getUserId())
                .state(test.getState())
                .type(test.getType())
                .countUsers(test.getCountUsers())
                .comment(test.getComment())
                .totalDuration(test.getTotalDuration())
                .fromDate(test.getFromDate())
                .toDate(test.getToDate())
                .version(test.getVersion())
                .build();
    }

    public TestPlan buildTestPlan(TestPlanReferencePrivateDto dto) {
        return TestPlan.builder()
                .ownerId(dto.getOwnerId())
                .jmxUri(dto.getFileLink())
                .fileName(extractFileName(dto.getFileLink()))
                .restrictionsLevel(dto.getRestrictionsLevel())
                .testingTool(dto.getTool())
                .fileExtension(dto.getFileExtension())
                .build();
    }

    public Resource buildResource(ResourceDto resourceDto) {
        return Resource.builder()
                .name(resourceDto.getName())
                .serverId(resourceDto.getServerId())
                .location(resourceDto.getLocation())
                .host(resourceDto.getHost())
                .port(resourceDto.getPort())
                .priority(resourceDto.getPriority())
                .maxUserCount(resourceDto.getMaxUserCount())
                .forFree(resourceDto.getForFree())
                .build();
    }

    public ResourceDto buildResourceDto(Resource resource) {
        return ResourceDto.builder()
                .name(resource.getName())
                .serverId(resource.getServerId())
                .location(resource.getLocation())
                .host(resource.getHost())
                .port(resource.getPort())
                .priority(resource.getPriority())
                .maxUserCount(resource.getMaxUserCount())
                .forFree(resource.getForFree())
                .build();
    }

    private String extractFileName(String uri) {
        return uri.substring(uri.lastIndexOf('/') + 1);
    }

}
