package com.julietamarateo.photography.dto;

import com.julietamarateo.photography.entity.CoverPhoto;
import java.time.LocalDateTime;

public class CoverPhotoDto {

    private String photoId;

    private String imageUrl;

    private String title;

    private String category;

    private String description;

    private LocalDateTime updatedAt;

    public CoverPhotoDto() {
    }

    public CoverPhotoDto(String photoId, String imageUrl, String title, String category, String description) {
        this.photoId = photoId;
        this.imageUrl = imageUrl;
        this.title = title;
        this.category = category;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public static CoverPhotoDto fromEntity(CoverPhoto entity) {
        if (entity == null) return null;
        CoverPhotoDto dto = new CoverPhotoDto();
        dto.setPhotoId(entity.getPhotoId());
        dto.setImageUrl(entity.getImageUrl());
        dto.setTitle(entity.getTitle());
        dto.setCategory(entity.getCategory());
        dto.setDescription(entity.getDescription());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public void applyToEntity(CoverPhoto entity) {
        if (entity == null) return;
        if (this.photoId != null) {
            entity.setPhotoId(this.photoId.trim());
        }
        if (this.imageUrl != null && !this.imageUrl.isBlank()) {
            entity.setImageUrl(this.imageUrl.trim());
        }
        if (this.title != null) {
            entity.setTitle(this.title.trim());
        }
        if (this.category != null) {
            entity.setCategory(this.category.trim());
        }
        if (this.description != null) {
            entity.setDescription(this.description.trim());
        }
        entity.setUpdatedAt(LocalDateTime.now());
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
