package com.julietamarateo.photography.dto;

import com.julietamarateo.photography.entity.ServiceItem;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

public class ServiceItemDto {

    private String id;

    @NotBlank(message = "El título del servicio es obligatorio")
    private String title;

    private String description;

    private String imageUrl;

    private List<String> features = new ArrayList<>();

    private String whatsappUrl;

    private Double price;

    public ServiceItemDto() {
    }

    public static ServiceItemDto fromEntity(ServiceItem entity) {
        if (entity == null) return null;
        ServiceItemDto dto = new ServiceItemDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setImageUrl(entity.getImageUrl());
        dto.setFeatures(entity.getFeatures() != null ? new ArrayList<>(entity.getFeatures()) : new ArrayList<>());
        dto.setWhatsappUrl(entity.getWhatsappUrl());
        dto.setPrice(entity.getPrice());
        return dto;
    }

    public ServiceItem toEntity() {
        ServiceItem entity = new ServiceItem();
        entity.setId(this.id);
        entity.setTitle(this.title);
        entity.setDescription(this.description);
        entity.setImageUrl(this.imageUrl);
        entity.setFeatures(this.features != null ? new ArrayList<>(this.features) : new ArrayList<>());
        entity.setWhatsappUrl(this.whatsappUrl);
        entity.setPrice(this.price);
        return entity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public String getWhatsappUrl() {
        return whatsappUrl;
    }

    public void setWhatsappUrl(String whatsappUrl) {
        this.whatsappUrl = whatsappUrl;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
