package com.ganesh.booking_system.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ResourceResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean available;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ResourceResponse() {
    }

    public ResourceResponse(
            Long id,
            String name,
            String description,
            BigDecimal price,
            Boolean available,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.available = available;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}