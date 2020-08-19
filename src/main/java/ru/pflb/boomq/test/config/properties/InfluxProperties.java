package ru.pflb.boomq.test.config.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfluxProperties {

    private String login;
    private String password;
    private String imageName;
}
