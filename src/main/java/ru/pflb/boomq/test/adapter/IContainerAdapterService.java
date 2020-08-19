package ru.pflb.boomq.test.adapter;

import ru.pflb.boomq.model.influxservice.InfluxUserDto;
import ru.pflb.boomq.model.test.settings.TestSettingDto;
import ru.pflb.boomq.model.testrunner.AllocatedResourceDto;
import ru.pflb.boomq.model.testrunner.ContainerDto;
import ru.pflb.boomq.test.config.properties.InfluxProperties;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.model.entity.TestPlan;

import java.util.List;

public interface IContainerAdapterService {

    List<ContainerDto> buildContainers(List<AllocatedResourceDto> resources,
                                        Test test,
                                        TestPlan testPlan,
                                        TestSettingDto settingDto);

    static String getWriteUrl(InfluxProperties influxProperties, InfluxUserDto influxUserDto) {
        return influxUserDto.getHost() + "/write?u=" + influxProperties.getLogin() +
                "&p=" + influxProperties.getPassword() +
                "&db=" + influxUserDto.getDbName();
    }
}
