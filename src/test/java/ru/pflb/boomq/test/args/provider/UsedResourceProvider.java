package ru.pflb.boomq.test.args.provider;

import ru.pflb.boomq.model.testrunner.State;
import ru.pflb.boomq.test.model.entity.UsedResource;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class UsedResourceProvider {

    public static UsedResource getUsedResource(Long id) {
        return UsedResource.builder()
                .id(id)
                .serverId(id.toString())
                .containerId(UUID.randomUUID().toString())
                .state(State.EXECUTABLE)
                .countUsers(5L)
                .build();

    }

    public static List<UsedResource> getListUsedResource() {
        return Arrays.asList(
                getUsedResource(1L),
                getUsedResource(2L),
                getUsedResource(3L)
        );
    }
}