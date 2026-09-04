package com.julietamarateo.photography.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "photos")
public class Photo {

    @Id
    @Column(nullable = false, length = 64)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category; // 'Foto Producto', 'Paisajismo', 'Eventos'

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String dimensions;

    @Column(columnDefinition = "TEXT")
    private String technicalSheet;

    // Ficha técnica desglosada
    private String camera;
    private String lens;
    private String aperture;
    private String shutter;
    private String iso;

    @Column(nullable = false)
    private Boolean inStock = true;

    private String badge;

    private Boolean featured = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Photo() {
    }

    public Photo(String id, String title, String category, Double price, String imageUrl,
                 String description, String dimensions, String technicalSheet,
                 String camera, String lens, String aperture, String shutter, String iso,
                 Boolean inStock, String badge, Boolean featured) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
        this.dimensions = dimensions;
        this.technicalSheet = technicalSheet;
        this.camera = camera;
        this.lens = lens;
        this.aperture = aperture;
        this.shutter = shutter;
        this.iso = iso;
        this.inStock = inStock != null ? inStock : true;
        this.badge = badge;
        this.featured = featured != null ? featured : false;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getTechnicalSheet() {
        return technicalSheet;
    }

    public void setTechnicalSheet(String technicalSheet) {
        this.technicalSheet = technicalSheet;
    }

    public String getCamera() {
        return camera;
    }

    public void setCamera(String camera) {
        this.camera = camera;
    }

    public String getLens() {
        return lens;
    }

    public void setLens(String lens) {
        this.lens = lens;
    }

    public String getAperture() {
        return aperture;
    }

    public void setAperture(String aperture) {
        this.aperture = aperture;
    }

    public String getShutter() {
        return shutter;
    }

    public void setShutter(String shutter) {
        this.shutter = shutter;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public Boolean getInStock() {
        return inStock;
    }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
