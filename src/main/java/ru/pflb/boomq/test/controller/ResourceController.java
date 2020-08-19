package ru.pflb.boomq.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.pflb.boomq.model.testrunner.ResourceDto;
import ru.pflb.boomq.test.service.IResourceService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/resource")
public class ResourceController {

    private IResourceService resourceService;

    public ResourceController(IResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Long> addResource(@RequestBody @Valid ResourceDto resourceDto) {
        log.info("try create resource. resourceDto: {}", resourceDto);

        Long id = resourceService.createResource(resourceDto);

        log.info("successful create resource with id: {}", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ResourceDto> getResource(@PathVariable Long id) {
        log.info("try get resource with id = {}", id);

        ResourceDto resource = resourceService.getResource(id);

        log.info("successful get resource with id = {}", id);
        return ResponseEntity.ok(resource);
    }
}