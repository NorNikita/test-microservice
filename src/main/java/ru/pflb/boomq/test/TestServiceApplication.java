package ru.pflb.boomq.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.pflb.boomq.test.config.properties.TestServiceProperties;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableConfigurationProperties(value = TestServiceProperties.class)
@EnableScheduling
@EnableAsync
public class TestServiceApplication {
    public static void main(String ... args) {
        SpringApplication.run(TestServiceApplication.class, args);
    }
}
