package ru.pflb.boomq.test.args.provider;

import ru.pflb.boomq.model.testrunner.AllocatedResourceDto;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AllocatedResourceDtoProvider {

    public static AllocatedResourceDto getAllocatedResourceDto() {
        Long countUsers = ThreadLocalRandom.current().nextLong(1, 10);

        return AllocatedResourceDto.builder()
                .serverId("1")
                .location("Moscow")
                .allocatedCountUser(countUsers)
                .build();
    }

    public static List<AllocatedResourceDto> getListAllocatedResourceDto() {
        return Arrays.asList(getAllocatedResourceDto(), getAllocatedResourceDto(), getAllocatedResourceDto());
    }
}