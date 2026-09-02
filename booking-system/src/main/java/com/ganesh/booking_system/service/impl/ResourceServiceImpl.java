package com.ganesh.booking_system.service.impl;

import com.ganesh.booking_system.dto.ResourceRequest;
import com.ganesh.booking_system.dto.ResourceResponse;
import com.ganesh.booking_system.entity.Resource;
import com.ganesh.booking_system.repository.ResourceRepository;
import com.ganesh.booking_system.service.ResourceService;
import com.ganesh.booking_system.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceServiceImpl(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Override
    public ResourceResponse createResource(ResourceRequest request) {

        Resource resource = new Resource();

        resource.setName(request.getName());
        resource.setDescription(request.getDescription());
        resource.setPrice(request.getPrice());
        resource.setAvailable(request.getAvailable());

        Resource savedResource = resourceRepository.save(resource);

        return mapToResponse(savedResource);
    }

    @Override
    public List<ResourceResponse> getAllResources() {

        return resourceRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ResourceResponse getResourceById(Long id) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resource not found with id: " + id
                        )
                );

        return mapToResponse(resource);
    }

    @Override
    public ResourceResponse updateResource(
            Long id,
            ResourceRequest request) {

        Resource existingResource = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resource not found with id: " + id
                        )
                );

        existingResource.setName(request.getName());
        existingResource.setDescription(request.getDescription());
        existingResource.setPrice(request.getPrice());
        existingResource.setAvailable(request.getAvailable());

        Resource updatedResource =
                resourceRepository.save(existingResource);

        return mapToResponse(updatedResource);
    }

    @Override
    public void deleteResource(Long id) {

        Resource existingResource = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resource not found with id: " + id
                        )
                );

        resourceRepository.delete(existingResource);
    }

    private ResourceResponse mapToResponse(Resource resource) {

        return new ResourceResponse(
                resource.getId(),
                resource.getName(),
                resource.getDescription(),
                resource.getPrice(),
                resource.getAvailable(),
                resource.getCreatedAt(),
                resource.getUpdatedAt()
        );
    }
}