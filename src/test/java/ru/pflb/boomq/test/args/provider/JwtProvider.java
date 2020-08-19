package ru.pflb.boomq.test.args.provider;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class JwtProvider {

    public static Jwt getJwt() throws IOException {

        String jwtToken = new String(
                Files.readAllBytes(
                        ResourceUtils.getFile("classpath:jwt.txt").toPath()
                )
        );

        return Jwt
                .withTokenValue(jwtToken)
                .header("tip", "JWT")
                .claim("user_id", 1L)
                .build();
    }
}
