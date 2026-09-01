package com.ganesh.booking_system.controller;

import com.ganesh.booking_system.dto.ResourceRequest;
import com.ganesh.booking_system.dto.ResourceResponse;
import com.ganesh.booking_system.service.ResourceService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    // ADMIN only
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResourceResponse> createResource(
            @Valid @RequestBody ResourceRequest request) {

        ResourceResponse response =
                resourceService.createResource(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ADMIN + USER
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<ResourceResponse>> getAllResources() {

        return ResponseEntity.ok(
                resourceService.getAllResources()
        );
    }

    // ADMIN + USER
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ResourceResponse> getResourceById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                resourceService.getResourceById(id)
        );
    }

    // ADMIN only
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResourceResponse> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody ResourceRequest request) {

        return ResponseEntity.ok(
                resourceService.updateResource(id, request)
        );
    }

    // ADMIN only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteResource(
            @PathVariable Long id) {

        resourceService.deleteResource(id);

        return ResponseEntity.noContent().build();
    }
}