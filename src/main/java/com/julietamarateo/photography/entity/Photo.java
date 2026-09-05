package com.julietamarateo.photography.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "photos", indexes = {
    @Index(name = "idx_photos_category", columnList = "category"),
    @Index(name = "idx_photos_featured", columnList = "featured"),
    @Index(name = "idx_photos_created_at", columnList = "createdAt")
})
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
    private String thumbnailUrl;

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

    private Double x;

    private Double y;

    private Double width;

    private Double height;

    @Column(name = "z_index")
    private Integer zIndex = 1;

    @Column(name = "rotation")
    private Double rotation = 0.0;

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

    public String getThumbnailUrl() {
        return thumbnailUrl != null && !thumbnailUrl.isBlank() ? thumbnailUrl : imageUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
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

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Integer getZIndex() {
        return zIndex;
    }

    public void setZIndex(Integer zIndex) {
        this.zIndex = zIndex;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Double getRotation() {
        return rotation != null ? rotation : 0.0;
    }

    public void setRotation(Double rotation) {
        this.rotation = rotation != null ? rotation : 0.0;
    }
}
