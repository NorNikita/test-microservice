package ru.pflb.boomq.test.args.provider;

import ru.pflb.boomq.model.influxservice.InfluxUserDto;

import java.util.UUID;

public class InfluxUserDtoProvider {

    public static InfluxUserDto getInfluxUserDto() {
        return InfluxUserDto.builder()
                .name("some.user@yandex.ru")
                .host("influx")
                .dbName("some.user@yandex.ru")
                .password(UUID.randomUUID().toString().substring(0,10))
                .build();
    }
}
