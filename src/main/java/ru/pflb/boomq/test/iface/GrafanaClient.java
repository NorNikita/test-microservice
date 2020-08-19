package ru.pflb.boomq.test.iface;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.pflb.boomq.model.grafanaservice.dto.GrafanaTestInfoDto;
import ru.pflb.boomq.model.grafanaservice.dto.RedirectUrlDto;
import ru.pflb.boomq.test.iface.configurations.GrafanaControllerConfig;

@FeignClient(name = "grafana-service", configuration = GrafanaControllerConfig.class)
@RequestMapping("/grafana-srv")
public interface GrafanaClient {

    @PostMapping("/auth/{login}")
    RedirectUrlDto auth(@PathVariable String login,
                        @RequestBody GrafanaTestInfoDto testInfo);
}
