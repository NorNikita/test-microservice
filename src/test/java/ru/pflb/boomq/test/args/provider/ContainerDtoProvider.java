package ru.pflb.boomq.test.args.provider;

import ru.pflb.boomq.model.testrunner.ContainerDto;

import java.util.Arrays;
import java.util.UUID;

public class ContainerDtoProvider {

    public static ContainerDto getContainerDto() {
        return ContainerDto.builder()
                .id(UUID.randomUUID().toString())
                .serverId("1")
                .image("pflb/jmeter_runner")
                .props(ContainerDto.Properties.builder()
                        .cmd(Arrays.asList(
                                "-n",
                                "-t",
                                "/d7139c2a-7eeb-47ef-89b2-ede865885bf9.jmx",
                                "-Jboomq_location=Moscow",
                                "-JinfluxdbUrl=http://192.168.1.162:8086/write?u=jmeter&p=tenacity2981_salon&db=nor11.nikit11@yandex.ru",
                                "-Jboomq_user=nor11.nikit11@yandex.ru",
                                "-Jboomq_applicationId=8",
                                "-Jboomq_threads=5",
                                "-Jboomq_rampup=60",
                                "-Jboomq_testId=22",
                                "-Jduration=360.0",
                                "-LERROR"
                        ))
                        .build())
                .build();
    }
}
