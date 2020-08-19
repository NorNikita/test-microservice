package ru.pflb.boomq.test.iface;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.pflb.boomq.model.influxservice.InfluxUserDto;
import ru.pflb.boomq.test.iface.configurations.InfluxControllerConfiguration;

@FeignClient(name = "influx-service", configuration = InfluxControllerConfiguration.class)
@RequestMapping("/influx-srv")
public interface InfluxClient {

    @GetMapping("/user/{userName}")
    InfluxUserDto getInfluxUser(@PathVariable String userName);

}
