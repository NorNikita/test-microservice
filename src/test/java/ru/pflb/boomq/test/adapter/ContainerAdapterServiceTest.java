package ru.pflb.boomq.test.adapter;

import feign.FeignException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pflb.boomq.model.influxservice.InfluxUserDto;
import ru.pflb.boomq.model.test.exception.TestServiceException;
import ru.pflb.boomq.model.test.settings.TestSettingDto;
import ru.pflb.boomq.model.testrunner.AllocatedResourceDto;
import ru.pflb.boomq.model.testrunner.ContainerDto;
import ru.pflb.boomq.test.adapter.containers.ContainerAdapterService;
import ru.pflb.boomq.test.config.properties.InfluxProperties;
import ru.pflb.boomq.test.iface.InfluxClient;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.model.entity.TestPlan;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContainerAdapterServiceTest {

    @Mock
    private InfluxClient influxClient;

    @Mock
    private InfluxProperties influxProperties;

    @InjectMocks
    private ContainerAdapterService containerAdapterService;

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.adapter.ContainerAdapterServiceStreamArgs#getArgsFor_buildContainers")
    void buildContainers (List<AllocatedResourceDto> resources,
                          Test test,
                          TestPlan testPlan,
                          TestSettingDto settingDto,
                          InfluxUserDto influxUserDto){

        when(influxClient.getInfluxUser(test.getUserName())).thenReturn(influxUserDto);

        List<ContainerDto> containerDtos = containerAdapterService.buildContainers(resources, test, testPlan, settingDto);
        assertEquals(resources.size(), containerDtos.size());

        when(influxClient.getInfluxUser(test.getUserName())).thenThrow(FeignException.class);
        assertThrows(TestServiceException.class, () -> containerAdapterService.buildContainers(resources, test, testPlan, settingDto));
    }
}
