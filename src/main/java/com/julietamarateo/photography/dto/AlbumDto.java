package com.julietamarateo.photography.dto;

import com.julietamarateo.photography.entity.Album;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AlbumDto {

    private String id;

    @NotBlank(message = "El título o nombre del álbum es obligatorio")
    private String name;

    private String title;

    private String subtitle;

    private String category;

    private String description;

    private String coverImage;

    private String coverImageUrl;

    private Integer displayOrder = 0;

    private Integer order = 0;

    private List<AlbumPhotoDto> photos = new ArrayList<>();

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
        dto.setTitle(entity.getTitle());
        dto.setSubtitle(entity.getSubtitle());
        dto.setCategory(entity.getCategory() != null ? entity.getCategory() : entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCoverImage(entity.getCoverImage());
        dto.setCoverImageUrl(entity.getCoverImage());
        dto.setDisplayOrder(entity.getDisplayOrder());
        dto.setOrder(entity.getDisplayOrder());
        dto.setPhotoUrls(entity.getPhotoUrls() != null ? new ArrayList<>(entity.getPhotoUrls()) : new ArrayList<>());

        if (entity.getPhotos() != null) {
            dto.setPhotos(entity.getPhotos().stream()
                    .map(AlbumPhotoDto::fromEntity)
                    .collect(Collectors.toList()));
            dto.setCount(entity.getPhotos().size());
        } else {
            dto.setPhotos(new ArrayList<>());
            dto.setCount(0);
        }

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public Album toEntity() {
        Album entity = new Album();
        entity.setId(this.id);
        String finalName = this.title != null && !this.title.isBlank() ? this.title.trim() : (this.name != null ? this.name.trim() : "");
        entity.setName(finalName);
        entity.setSubtitle(this.subtitle != null ? this.subtitle.trim() : null);
        entity.setCategory(this.category != null ? this.category.trim() : finalName);
        entity.setDescription(this.description != null ? this.description.trim() : "");
        String finalCover = this.coverImageUrl != null && !this.coverImageUrl.isBlank() ? this.coverImageUrl.trim() : (this.coverImage != null ? this.coverImage.trim() : "");
        entity.setCoverImage(finalCover);
        Integer finalOrder = this.order != null && this.order != 0 ? this.order : (this.displayOrder != null ? this.displayOrder : 0);
        entity.setDisplayOrder(finalOrder);
        entity.setPhotoUrls(this.photoUrls != null ? new ArrayList<>(this.photoUrls) : new ArrayList<>());
        entity.setCreatedAt(this.createdAt != null ? this.createdAt : LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

    public void applyToEntity(Album entity) {
        if (entity == null) return;
        String finalName = this.title != null && !this.title.isBlank() ? this.title.trim() : (this.name != null ? this.name.trim() : null);
        if (finalName != null && !finalName.isBlank()) {
            entity.setName(finalName);
        }
        if (this.subtitle != null) {
            entity.setSubtitle(this.subtitle.trim());
        }
        if (this.category != null && !this.category.isBlank()) {
            entity.setCategory(this.category.trim());
        } else if (finalName != null && !finalName.isBlank()) {
            entity.setCategory(finalName);
        }
        if (this.description != null) {
            entity.setDescription(this.description.trim());
        }
        String finalCover = this.coverImageUrl != null && !this.coverImageUrl.isBlank() ? this.coverImageUrl.trim() : (this.coverImage != null ? this.coverImage.trim() : null);
        if (finalCover != null) {
            entity.setCoverImage(finalCover);
        }
        Integer finalOrder = this.order != null && this.order != 0 ? this.order : this.displayOrder;
        if (finalOrder != null) {
            entity.setDisplayOrder(finalOrder);
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
        return name != null ? name : title;
    }

    public void setName(String name) {
        this.name = name;
        if (this.title == null) this.title = name;
    }

    public String getTitle() {
        return title != null ? title : name;
    }

    public void setTitle(String title) {
        this.title = title;
        if (this.name == null) this.name = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
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
        return coverImage != null ? coverImage : coverImageUrl;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
        if (this.coverImageUrl == null) this.coverImageUrl = coverImage;
    }

    public String getCoverImageUrl() {
        return coverImageUrl != null ? coverImageUrl : coverImage;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
        if (this.coverImage == null) this.coverImage = coverImageUrl;
    }

    public Integer getDisplayOrder() {
        return displayOrder != null ? displayOrder : order;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
        if (this.order == null || this.order == 0) this.order = displayOrder;
    }

    public Integer getOrder() {
        return order != null ? order : displayOrder;
    }

    public void setOrder(Integer order) {
        this.order = order;
        if (this.displayOrder == null || this.displayOrder == 0) this.displayOrder = order;
    }

    public List<AlbumPhotoDto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<AlbumPhotoDto> photos) {
        this.photos = photos != null ? photos : new ArrayList<>();
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
