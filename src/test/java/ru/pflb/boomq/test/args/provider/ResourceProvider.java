package ru.pflb.boomq.test.args.provider;

import ru.pflb.boomq.test.model.entity.Resource;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ResourceProvider {
    public static Resource getResource() {
        return Resource.builder()
                .id(1L)
                .name("Name_" + UUID.randomUUID().toString().substring(0, 5))
                .serverId("1")
                .location("Moscow")
                .host("0.0.0.0")
                .port(new Random().nextInt())
                .forFree(true)
                .maxUserCount(7L)
                .priority(1L)
                .build();
    }

    public static Resource getFreeResource(Long id) {
        return Resource.builder()
                .id(id)
                .name("Name_" + UUID.randomUUID().toString().substring(0, 5))
                .serverId(id.toString())
                .location("Moscow")
                .host("0.0.0.0")
                .port(new Random().nextInt())
                .forFree(true)
                .maxUserCount(7L)
                .priority(1L)
                .build();
    }

    public static List<Resource> getListFreeResources() {
        return Arrays.asList(
                getFreeResource(1L),
                getFreeResource(2L),
                getFreeResource(3L)
        );
    }
}