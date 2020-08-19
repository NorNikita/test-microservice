package ru.pflb.boomq.test.args.provider;

import ru.pflb.boomq.model.testrunner.FreeResourceDto;

import java.util.Arrays;
import java.util.List;

public class FreeResourceDtoProvider {

    public static FreeResourceDto getFreeResourceDto(String serverId) {
        return FreeResourceDto.builder()
                .serverId(serverId)
                .isAvailable(true)
                .build();
    }

    public static List<FreeResourceDto> getListFreeResourcesDto() {
        return Arrays.asList(
                getFreeResourceDto("1"),
                getFreeResourceDto("2"),
                getFreeResourceDto("3")
        );
    }
}
