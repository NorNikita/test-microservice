package ru.pflb.boomq.test.iface;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.pflb.boomq.model.testrunner.ContainerDto;
import ru.pflb.boomq.model.testrunner.ContainerState;
import ru.pflb.boomq.model.testrunner.FreeResourceDto;
import ru.pflb.boomq.model.testrunner.Server;
import ru.pflb.boomq.test.iface.configurations.TestRunnerControllerConfig;

import java.util.List;

@FeignClient(name = "Test-Runner-Client", url = "${boomq.test-run.host}", configuration = TestRunnerControllerConfig.class)
public interface TestRunnerClient {

    @PostMapping("/api/v2/server")
    Server createServer(@RequestBody Server server);

    @GetMapping(value = "/api/v2/server/status")
    List<FreeResourceDto> getResourcesStates(@RequestParam(name = "id") List<String> serverId);

    @PostMapping(value = "/api/v2/server/{serverId}/container")
    ContainerDto createContainer(@PathVariable String serverId,
                                 @RequestBody ContainerDto container);

    @PutMapping(value = "/api/v2/server/{serverId}/container/{containerId}/start")
    ContainerState startContainer(@PathVariable String serverId,
                                  @PathVariable String containerId);

    @PutMapping("/api/v2/server/{serverId}/container/{containerId}/stop")
    ContainerState stopContainer(@PathVariable String serverId,
                                 @PathVariable String containerId);

    @GetMapping("/api/v2/server/{serverId}/container/{containerId}/status")
    ContainerState getStateContainer(@PathVariable String serverId,
                                     @PathVariable String containerId);
}

