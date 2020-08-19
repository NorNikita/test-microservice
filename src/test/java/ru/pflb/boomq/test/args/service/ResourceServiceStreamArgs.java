package ru.pflb.boomq.test.args.service;

import org.junit.jupiter.params.provider.Arguments;
import ru.pflb.boomq.test.args.provider.ResourceDtoProvider;
import ru.pflb.boomq.test.args.provider.ResourceProvider;
import ru.pflb.boomq.test.args.provider.ServerProvider;

import java.util.stream.Stream;

public class ResourceServiceStreamArgs {

    public static Stream<Arguments> getArgsFor_createResource() {
        return Stream.of(
                Arguments.of(
                        ResourceDtoProvider.getResourceDto(),
                        ResourceProvider.getResource(),
                        ServerProvider.getServer()
                )
        );
    }

    public static Stream<Arguments> getArgsFor_getResource() {
        return Stream.of(
                Arguments.of(
                        ResourceProvider.getResource(),
                        ResourceDtoProvider.getResourceDto()
                )
        );
    }
}
