package com.julietamarateo.photography.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.julietamarateo.photography.entity.PhysicalPhoto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PhysicalPhotoDto {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private String id;

    @NotBlank(message = "El título de la foto es obligatorio")
    private String title;

    private String imageUrl;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser un valor positivo")
    private Double price;

    private String currency = "ARS";

    private Boolean isActive = true;

    private Boolean isSoldOut = false;

    private String mercadoPagoUrl;

    private List<CustomAttributeDto> customAttributes = new ArrayList<>();

    private Integer displayOrder = 0;

    private LocalDateTime createdAt;

    public PhysicalPhotoDto() {
    }

    public static PhysicalPhotoDto fromEntity(PhysicalPhoto entity) {
        if (entity == null) return null;
        PhysicalPhotoDto dto = new PhysicalPhotoDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setImageUrl(entity.getImageUrl());
        dto.setPrice(entity.getPrice());
        dto.setCurrency(entity.getCurrency() != null ? entity.getCurrency() : "ARS");
        dto.setIsActive(entity.getIsActive() != null ? entity.getIsActive() : true);
        dto.setIsSoldOut(entity.getIsSoldOut() != null ? entity.getIsSoldOut() : false);
        dto.setMercadoPagoUrl(entity.getMercadoPagoUrl());
        dto.setDisplayOrder(entity.getDisplayOrder() != null ? entity.getDisplayOrder() : 0);
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getCustomAttributesJson() != null && !entity.getCustomAttributesJson().isBlank()) {
            try {
                List<CustomAttributeDto> attrs = OBJECT_MAPPER.readValue(
                        entity.getCustomAttributesJson(),
                        new TypeReference<List<CustomAttributeDto>>() {}
                );
                dto.setCustomAttributes(attrs != null ? attrs : new ArrayList<>());
            } catch (Exception e) {
                dto.setCustomAttributes(new ArrayList<>());
            }
        } else {
            dto.setCustomAttributes(new ArrayList<>());
        }

        return dto;
    }

    public PhysicalPhoto toEntity() {
        PhysicalPhoto entity = new PhysicalPhoto();
        entity.setId(this.id);
        entity.setTitle(this.title);
        entity.setImageUrl(this.imageUrl);
        entity.setPrice(this.price);
        entity.setCurrency(this.currency != null ? this.currency : "ARS");
        entity.setIsActive(this.isActive != null ? this.isActive : true);
        entity.setIsSoldOut(this.isSoldOut != null ? this.isSoldOut : false);
        entity.setMercadoPagoUrl(this.mercadoPagoUrl);
        entity.setDisplayOrder(this.displayOrder != null ? this.displayOrder : 0);

        if (this.customAttributes != null && !this.customAttributes.isEmpty()) {
            try {
                entity.setCustomAttributesJson(OBJECT_MAPPER.writeValueAsString(this.customAttributes));
            } catch (Exception e) {
                entity.setCustomAttributesJson("[]");
            }
        } else {
            entity.setCustomAttributesJson("[]");
        }

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsSoldOut() {
        return isSoldOut;
    }

    public void setIsSoldOut(Boolean isSoldOut) {
        this.isSoldOut = isSoldOut;
    }

    public String getMercadoPagoUrl() {
        return mercadoPagoUrl;
    }

    public void setMercadoPagoUrl(String mercadoPagoUrl) {
        this.mercadoPagoUrl = mercadoPagoUrl;
    }

    public List<CustomAttributeDto> getCustomAttributes() {
        if (customAttributes == null) {
            customAttributes = new ArrayList<>();
        }
        return customAttributes;
    }

    public void setCustomAttributes(List<CustomAttributeDto> customAttributes) {
        this.customAttributes = customAttributes;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
