package com.julietamarateo.photography.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "physical_photos", indexes = {
    @Index(name = "idx_physical_photos_active", columnList = "isActive"),
    @Index(name = "idx_physical_photos_order", columnList = "displayOrder"),
    @Index(name = "idx_physical_photos_created", columnList = "createdAt")
})
public class PhysicalPhoto {

    @Id
    @Column(nullable = false, length = 64)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false, length = 10)
    private String currency = "ARS";

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Boolean isSoldOut = false;

    @Column(columnDefinition = "TEXT")
    private String mercadoPagoUrl;

    /**
     * Serialización JSON de atributos libres Key-Value:
     * [{"label": "Tamaño", "value": "30x40 cm"}, {"label": "Papel", "value": "Fine Art"}]
     */
    @Column(columnDefinition = "TEXT")
    private String customAttributesJson;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public PhysicalPhoto() {
    }

    public PhysicalPhoto(String id, String title, String imageUrl, Double price, String currency,
                         Boolean isActive, Boolean isSoldOut, String mercadoPagoUrl,
                         String customAttributesJson, Integer displayOrder) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.price = price;
        this.currency = currency != null ? currency : "ARS";
        this.isActive = isActive != null ? isActive : true;
        this.isSoldOut = isSoldOut != null ? isSoldOut : false;
        this.mercadoPagoUrl = mercadoPagoUrl;
        this.customAttributesJson = customAttributesJson;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
        this.createdAt = LocalDateTime.now();
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

    public String getCustomAttributesJson() {
        return customAttributesJson;
    }

    public void setCustomAttributesJson(String customAttributesJson) {
        this.customAttributesJson = customAttributesJson;
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
