package com.julietamarateo.photography.dto;

import com.julietamarateo.photography.entity.Album;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AlbumDto {

    private String id;

    @NotBlank(message = "El nombre del álbum es obligatorio")
    private String name;

    private String category;

    private String description;

    private String coverImage;

    private Integer displayOrder = 0;

    private List<String> photoUrls = new ArrayList<>();

    private Integer count = 0;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public AlbumDto() {
    }

    public static AlbumDto fromEntity(Album entity) {
        if (entity == null) return null;
        AlbumDto dto = new AlbumDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCategory(entity.getCategory() != null ? entity.getCategory() : entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCoverImage(entity.getCoverImage());
        dto.setDisplayOrder(entity.getDisplayOrder());
        dto.setPhotoUrls(entity.getPhotoUrls() != null ? new ArrayList<>(entity.getPhotoUrls()) : new ArrayList<>());
        dto.setCount(entity.getPhotoUrls() != null ? entity.getPhotoUrls().size() : 0);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public Album toEntity() {
        Album entity = new Album();
        entity.setId(this.id);
        entity.setName(this.name != null ? this.name.trim() : "");
        entity.setCategory(this.category != null ? this.category.trim() : (this.name != null ? this.name.trim() : ""));
        entity.setDescription(this.description != null ? this.description.trim() : "");
        entity.setCoverImage(this.coverImage != null ? this.coverImage.trim() : "");
        entity.setDisplayOrder(this.displayOrder != null ? this.displayOrder : 0);
        entity.setPhotoUrls(this.photoUrls != null ? new ArrayList<>(this.photoUrls) : new ArrayList<>());
        entity.setCreatedAt(this.createdAt != null ? this.createdAt : LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

    public void applyToEntity(Album entity) {
        if (entity == null) return;
        if (this.name != null && !this.name.isBlank()) {
            entity.setName(this.name.trim());
        }
        if (this.category != null && !this.category.isBlank()) {
            entity.setCategory(this.category.trim());
        } else if (this.name != null && !this.name.isBlank()) {
            entity.setCategory(this.name.trim());
        }
        if (this.description != null) {
            entity.setDescription(this.description.trim());
        }
        if (this.coverImage != null) {
            entity.setCoverImage(this.coverImage.trim());
        }
        if (this.displayOrder != null) {
            entity.setDisplayOrder(this.displayOrder);
        }
        if (this.photoUrls != null) {
            entity.setPhotoUrls(new ArrayList<>(this.photoUrls));
        }
        entity.setUpdatedAt(LocalDateTime.now());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls != null ? photoUrls : new ArrayList<>();
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
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
