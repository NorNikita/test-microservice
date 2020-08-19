package ru.pflb.boomq.test.config.properties;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Minio {

    private String endpoint;
    private String accesskey;
    private String secretkey;
    private String bucketname;
}
