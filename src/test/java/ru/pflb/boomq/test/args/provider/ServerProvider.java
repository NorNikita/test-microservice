package ru.pflb.boomq.test.args.provider;

import ru.pflb.boomq.model.testrunner.Server;

import java.util.Random;

public class ServerProvider {

    public static Server getServer() {
        return Server.builder()
                .id("1")
                .ip("0.0.0.0")
                .port(new Random().nextInt())
                .name("somename")
                .build();
    }
}