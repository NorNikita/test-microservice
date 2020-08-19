package ru.pflb.boomq.test.args.provider;

import org.springframework.http.HttpMethod;
import ru.pflb.boomq.model.projectservice.dto.testproject.Group;
import ru.pflb.boomq.model.projectservice.dto.testproject.Request;
import ru.pflb.boomq.model.test.testprofile.TestProfileDto;

import java.util.Collections;

public class TestProfileDtoProvider {

    public static TestProfileDto getTestProfileDto() {
        return TestProfileDto.builder()
                .groups(Collections.singletonList(buildGroup()))
                .build();
    }

    private static Group buildGroup() {
        return Group.builder()
                .request(buildRequest())
                .perc(100.0)
                .name("New group 1")
                .build();
    }

    private static Request buildRequest() {
        return Request.builder()
                .url("https://yandex.ru")
                .method(HttpMethod.GET)
                .build();
    }
}