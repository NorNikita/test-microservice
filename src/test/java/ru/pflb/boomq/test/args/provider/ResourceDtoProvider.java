package ru.pflb.boomq.test.args.provider;

import ru.pflb.boomq.model.testrunner.ResourceDto;

import java.util.Random;
import java.util.UUID;

public class ResourceDtoProvider {

    public static ResourceDto getResourceDto() {
        return  ResourceDto.builder()
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
}
