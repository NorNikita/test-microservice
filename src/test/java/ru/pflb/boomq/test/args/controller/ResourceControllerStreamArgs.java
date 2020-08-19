package ru.pflb.boomq.test.args.controller;

import org.junit.jupiter.params.provider.Arguments;
import ru.pflb.boomq.test.args.provider.AuthorityProvider;
import ru.pflb.boomq.test.args.provider.JwtProvider;
import ru.pflb.boomq.test.args.provider.ResourceDtoProvider;
import ru.pflb.boomq.test.args.provider.ServerProvider;

import java.io.IOException;
import java.util.stream.Stream;

public class ResourceControllerStreamArgs {

    public static Stream<Arguments> getArgsFor_createResource() throws IOException {
        return Stream.of(
                Arguments.of(
                        JwtProvider.getJwt(),
                        AuthorityProvider.getAdminAuthority(),
                        ResourceDtoProvider.getResourceDto(),
                        ServerProvider.getServer()
                )
        );
    }
}