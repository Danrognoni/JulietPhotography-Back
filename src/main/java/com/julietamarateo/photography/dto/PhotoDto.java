package com.julietamarateo.photography.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.julietamarateo.photography.entity.Photo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PhotoDto {

    private String id;

    @NotBlank(message = "El título de la foto es obligatorio")
    private String title;

    @NotBlank(message = "La categoría es obligatoria")
    private String category;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser un valor positivo")
    private Double price;

    private String imageUrl;

    private String thumbnailUrl;

    private String description;

    private String dimensions;

    private String technicalSheet;

    private CameraSpecsDto cameraDetails;

    private Boolean inStock = true;

    private String badge;

    private Boolean featured = false;

    private Double x;

    private Double y;

    private Double width;

    private Double height;

    @JsonProperty("rotation")
    @JsonAlias({"rotation", "rotate", "rot"})
    private Double rotation = 0.0;

    private Integer zIndex = 1;

    public PhotoDto() {
    }

    public static PhotoDto fromEntity(Photo entity) {
        if (entity == null) return null;
        PhotoDto dto = new PhotoDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setCategory(entity.getCategory());
        dto.setPrice(entity.getPrice());
        dto.setImageUrl(entity.getImageUrl());
        dto.setThumbnailUrl(entity.getThumbnailUrl());
        dto.setDescription(entity.getDescription());
        dto.setDimensions(entity.getDimensions());
        dto.setTechnicalSheet(entity.getTechnicalSheet());
        dto.setInStock(entity.getInStock());
        dto.setBadge(entity.getBadge());
        dto.setFeatured(entity.getFeatured());
        dto.setX(entity.getX());
        dto.setY(entity.getY());
        dto.setWidth(entity.getWidth());
        dto.setHeight(entity.getHeight());
        dto.setRotation(entity.getRotation() != null ? entity.getRotation() : 0.0);
        dto.setZIndex(entity.getZIndex() != null ? entity.getZIndex() : 1);

        if (entity.getCamera() != null || entity.getLens() != null ||
            entity.getAperture() != null || entity.getShutter() != null || entity.getIso() != null) {
            dto.setCameraDetails(new CameraSpecsDto(
                    entity.getCamera(),
                    entity.getLens(),
                    entity.getAperture(),
                    entity.getShutter(),
                    entity.getIso()
            ));
        }

        return dto;
    }

    public Photo toEntity() {
        Photo entity = new Photo();
        entity.setId(this.id);
        entity.setTitle(this.title);
        entity.setCategory(this.category);
        entity.setPrice(this.price);
        entity.setImageUrl(this.imageUrl);
        entity.setThumbnailUrl(this.thumbnailUrl != null ? this.thumbnailUrl : this.imageUrl);
        entity.setDescription(this.description);
        entity.setDimensions(this.dimensions);
        entity.setTechnicalSheet(this.technicalSheet);
        entity.setInStock(this.inStock != null ? this.inStock : true);
        entity.setBadge(this.badge);
        entity.setFeatured(this.featured != null ? this.featured : false);
        entity.setX(this.x);
        entity.setY(this.y);
        entity.setWidth(this.width);
        entity.setHeight(this.height);
        entity.setRotation(this.rotation != null ? this.rotation : 0.0);
        entity.setZIndex(this.zIndex != null ? this.zIndex : 1);

        if (this.cameraDetails != null) {
            entity.setCamera(this.cameraDetails.getCamera());
            entity.setLens(this.cameraDetails.getLens());
            entity.setAperture(this.cameraDetails.getAperture());
            entity.setShutter(this.cameraDetails.getShutter());
            entity.setIso(this.cameraDetails.getIso());
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

    public CameraSpecsDto getCameraDetails() {
        if (this.cameraDetails == null) {
            this.cameraDetails = new CameraSpecsDto();
        }
        return cameraDetails;
    }

    public void setCameraDetails(CameraSpecsDto cameraDetails) {
        this.cameraDetails = cameraDetails;
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

    public Double getRotation() {
        return rotation != null ? rotation : 0.0;
    }

    public void setRotation(Double rotation) {
        this.rotation = rotation != null ? rotation : 0.0;
    }
}
