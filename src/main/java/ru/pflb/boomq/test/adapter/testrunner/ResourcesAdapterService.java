package ru.pflb.boomq.test.adapter.testrunner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.pflb.boomq.model.testrunner.AllocatedResourceDto;
import ru.pflb.boomq.model.testrunner.FreeResourceDto;
import ru.pflb.boomq.model.testrunner.Server;
import ru.pflb.boomq.model.testrunner.State;
import ru.pflb.boomq.model.test.ExceptionMessage;
import ru.pflb.boomq.model.test.exception.TestServiceException;
import ru.pflb.boomq.test.adapter.IResourcesAdapterService;
import ru.pflb.boomq.test.iface.TestRunnerClient;
import ru.pflb.boomq.test.model.entity.Resource;
import ru.pflb.boomq.test.model.entity.Test;
import ru.pflb.boomq.test.model.entity.UsedResource;
import ru.pflb.boomq.test.repository.ResourceRepository;
import ru.pflb.boomq.test.repository.UsedResourceRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ResourcesAdapterService implements IResourcesAdapterService {

    private ResourceRepository resourceRepository;
    private TestRunnerClient testRunnerClient;
    private UsedResourceRepository usedResourceRepository;

    public ResourcesAdapterService(ResourceRepository resourceRepository,
                                   TestRunnerClient testRunnerClient,
                                   UsedResourceRepository usedResourceRepository) {
        this.resourceRepository = resourceRepository;
        this.testRunnerClient = testRunnerClient;
        this.usedResourceRepository = usedResourceRepository;
    }

    @Override
    @Transactional
    public List<AllocatedResourceDto> allocateResources(Test test) {

        Long countUsers = test.getCountUsers();

        //вытаскиваем все сервера-ресурсы для бесплатной подиски
        List<Resource> resources = resourceRepository.findAllByForFree(true);

        List<String> serverIds = resources.stream()
                .map(Resource::getServerId)
                .collect(Collectors.toList());

        //узнаем какие из них доступны
        List<String> availableServerIds = testRunnerClient.getResourcesStates(serverIds)
                .stream()
                .filter(FreeResourceDto::isAvailable)
                .map(FreeResourceDto::getServerId)
                .collect(Collectors.toList());

        //узнаем как загружены доступные используемые ресурсы
        Map<String, Long> loadResources = CollectionUtils.isEmpty(availableServerIds) ? new HashMap<>() :
                usedResourceRepository.findAllByServerIdInAndState(availableServerIds, State.EXECUTABLE)
                        .stream()
                        .collect(Collectors.toMap(UsedResource::getServerId, UsedResource::getCountUsers, (u,v) -> u + v)); //null ловится

        // в итоге используем только те, которые доступны и имееют ненулевое количество доступных пользователей
        resources = resources.stream()
                .filter(resource -> availableServerIds.contains(resource.getServerId()))
                .filter(resource -> resource.getMaxUserCount() - loadResources.getOrDefault(resource.getServerId(), 0L) > 0)
                .collect(Collectors.toList());

        if (countUsers <= totalAvailableUsers(resources, loadResources)) {
            return allocateResources(countUsers, resources, loadResources);
        } else {
            return null;
        }
    }

    @Override
    public Server createServer(Resource resource) {
        log.info("try create new server! resource: {}", resource);

        Server createdServer = testRunnerClient.createServer(
                Server.builder().id(resource.getServerId())
                        .name(resource.getName())
                        .ip(resource.getHost())
                        .port(resource.getPort())
                        .build()
        );

        log.info("new server created! server: {}", createdServer);
        return createdServer;
    }

    private long totalAvailableUsers(List<Resource> resources, Map<String, Long> loadResource) {
        return resources.stream()
                .map(resource -> resource.getMaxUserCount() - loadResource.getOrDefault(resource.getServerId(), 0L))
                .reduce(0L, (u,v) -> u + v);
    }

    private List<AllocatedResourceDto> allocateResources(long countUser, List<Resource> resources, Map<String, Long> loadResource) {
        Resource resource;
        List<AllocatedResourceDto> allocatedResources = new ArrayList<>();

        while(countUser > 0) {
            if(resources.size() > 0) {
                resource = resources.get(0);
                long availableUsers = resource.getMaxUserCount() - loadResource.getOrDefault(resource.getServerId(), 0L);
                long count = availableUsers > countUser ? countUser : availableUsers;
                countUser -= count;

                allocatedResources.add(AllocatedResourceDto.builder()
                        .serverId(resource.getServerId())
                        .location(resource.getLocation())
                        .allocatedCountUser(count)
                        .build());
            } else {
                throw new TestServiceException(ExceptionMessage.ERROR_RESOURCES_ALLOCATED);
            }
        }

        return allocatedResources;
    }
}
