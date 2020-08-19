package ru.pflb.boomq.test.adapter.containers;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pflb.boomq.model.influxservice.InfluxUserDto;
import ru.pflb.boomq.model.test.ExceptionMessage;
import ru.pflb.boomq.model.test.exception.TestServiceException;
import ru.pflb.boomq.model.test.settings.TestSettingDto;
import ru.pflb.boomq.model.testrunner.AllocatedResourceDto;
import ru.pflb.boomq.model.testrunner.ContainerDto;
import ru.pflb.boomq.test.adapter.IContainerAdapterService;
import ru.pflb.boomq.test.config.properties.InfluxProperties;
import ru.pflb.boomq.test.iface.InfluxClient;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.model.entity.TestPlan;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ContainerAdapterService implements IContainerAdapterService {

    private InfluxClient influxClient;
    private InfluxProperties influxProperties;

    public ContainerAdapterService(InfluxClient influxClient,
                                   InfluxProperties influxProperties) {
        this.influxClient = influxClient;
        this.influxProperties = influxProperties;
    }

    @Override
    public List<ContainerDto> buildContainers(List<AllocatedResourceDto> resources,
                                 Test test,
                                 TestPlan testPlan,
                                 TestSettingDto settingDto) {

        try {
            log.info("try build containers for test with id = {}", test.getTestId());

            final InfluxUserDto influxUserDto = influxClient.getInfluxUser(test.getUserName());
            String writeUrl = IContainerAdapterService.getWriteUrl(influxProperties, influxUserDto);
            String fileName = testPlan.getJmxUri().substring(testPlan.getJmxUri().lastIndexOf('/') + 1);

            return resources.stream()
                    .map(resource ->
                            ContainerDto.builder()
                                    .serverId(resource.getServerId())
                                    .image(influxProperties.getImageName())
                                    .props(ContainerDto.Properties.builder()
                                            .cmd(Arrays.asList(
                                                    "-n",
                                                    "-t",
                                                    "/" + fileName,
                                                    "-Jboomq_location=" + resource.getLocation(),
                                                    "-JinfluxdbUrl=" + writeUrl,
                                                    "-Jboomq_user=" + test.getUserName(),
                                                    "-Jboomq_applicationId=" + test.getProjectId(), //?
                                                    "-Jboomq_threads=" + resource.getAllocatedCountUser(),
                                                    "-Jboomq_rampup=" + settingDto.getRampUp(),
                                                    "-Jboomq_testId=" + test.getTestId(),
                                                    "-Jduration=" + test.getTotalDuration(),
                                                    "-LERROR"
                                            ))
                                            .files(Arrays.asList(
                                                    ContainerDto.File.builder()
                                                            .name(fileName)
                                                            .uri(testPlan.getJmxUri())
                                                            .build()
                                            ))
                                            .build())
                                    .build()
                    )
                    .collect(Collectors.toList());

        } catch (FeignException e) {
            log.error("failed build containers for test with id = {}", test.getTestId());
            throw new TestServiceException(ExceptionMessage.FAILED_BUILD_CONTAINERS);
        }
    }
}
