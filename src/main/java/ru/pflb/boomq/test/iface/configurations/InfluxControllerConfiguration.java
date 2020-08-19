package ru.pflb.boomq.test.iface.configurations;

import feign.RequestInterceptor;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

public class InfluxControllerConfiguration {

    private ClientCredentialsResourceDetails clientCredentialsResourceDetails;

    public InfluxControllerConfiguration(ClientCredentialsResourceDetails clientCredentialsResourceDetails) {
        this.clientCredentialsResourceDetails = clientCredentialsResourceDetails;
    }

    @Bean
    public RequestInterceptor getRequestInterceptor() {
        return new OAuth2FeignRequestInterceptor(new DefaultOAuth2ClientContext(), clientCredentialsResourceDetails);
    }
}
