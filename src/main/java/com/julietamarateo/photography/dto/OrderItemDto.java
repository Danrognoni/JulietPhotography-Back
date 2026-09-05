package com.julietamarateo.photography.dto;

import com.julietamarateo.photography.entity.OrderItem;

public class OrderItemDto {
    private Long id;
    private String photoId;
    private String photoTitle;
    private String photoCategory;
    private String photoImageUrl;
    private Integer quantity;
    private Double unitPrice;
    private Double subtotal;

    public OrderItemDto() {
    }

    public static OrderItemDto fromEntity(OrderItem entity) {
        if (entity == null) return null;
        OrderItemDto dto = new OrderItemDto();
        dto.setId(entity.getId());
        dto.setPhotoId(entity.getPhotoId());
        dto.setPhotoTitle(entity.getPhotoTitle());
        dto.setPhotoCategory(entity.getPhotoCategory());
        dto.setPhotoImageUrl(entity.getPhotoImageUrl());
        dto.setQuantity(entity.getQuantity());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setSubtotal(entity.getSubtotal());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getPhotoTitle() {
        return photoTitle;
    }

    public void setPhotoTitle(String photoTitle) {
        this.photoTitle = photoTitle;
    }

    public String getPhotoCategory() {
        return photoCategory;
    }

    public void setPhotoCategory(String photoCategory) {
        this.photoCategory = photoCategory;
    }

    public String getPhotoImageUrl() {
        return photoImageUrl;
    }

    public void setPhotoImageUrl(String photoImageUrl) {
        this.photoImageUrl = photoImageUrl;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
