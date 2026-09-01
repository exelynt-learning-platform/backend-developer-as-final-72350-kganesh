package com.ganesh.booking_system.service;

import com.ganesh.booking_system.dto.ResourceRequest;
import com.ganesh.booking_system.dto.ResourceResponse;

import java.util.List;

public interface ResourceService {

    ResourceResponse createResource(ResourceRequest request);

    List<ResourceResponse> getAllResources();

    ResourceResponse getResourceById(Long id);

    ResourceResponse updateResource(Long id, ResourceRequest request);

    void deleteResource(Long id);
}