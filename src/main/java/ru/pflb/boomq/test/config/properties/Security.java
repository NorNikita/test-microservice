package ru.pflb.boomq.test.config.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Security {

    private String clientId;
    private String clientSecret;
    private String accessTokenUri;
    private String grantType;
    private String scope;
}