package ru.pflb.boomq.test.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = "boomq")
public class TestServiceProperties {

    @NestedConfigurationProperty
    private Minio minio;

    @NestedConfigurationProperty
    private Security security;

    @NestedConfigurationProperty
    private InfluxProperties influxProperties;
}
