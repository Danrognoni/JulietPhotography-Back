package com.julietamarateo.photography.dto;

import com.julietamarateo.photography.entity.AlbumPhoto;
import java.time.LocalDateTime;

public class AlbumPhotoDto {

    private String id;
    private String albumId;
    private String imageUrl;
    private String caption;
    private String orientation = "portrait";
    private Integer displayOrder = 0;
    private LocalDateTime createdAt;

    public AlbumPhotoDto() {
    }

    public AlbumPhotoDto(String id, String albumId, String imageUrl, String caption, String orientation, Integer displayOrder, LocalDateTime createdAt) {
        this.id = id;
        this.albumId = albumId;
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.orientation = orientation != null ? orientation : "portrait";
        this.displayOrder = displayOrder != null ? displayOrder : 0;
        this.createdAt = createdAt;
    }

    public static AlbumPhotoDto fromEntity(AlbumPhoto entity) {
        if (entity == null) return null;
        AlbumPhotoDto dto = new AlbumPhotoDto();
        dto.setId(entity.getId());
        if (entity.getAlbum() != null) {
            dto.setAlbumId(entity.getAlbum().getId());
        }
        dto.setImageUrl(entity.getImageUrl());
        dto.setCaption(entity.getCaption());
        dto.setOrientation(entity.getOrientation());
        dto.setDisplayOrder(entity.getDisplayOrder());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Integer getOrder() {
        return displayOrder;
    }

    public void setOrder(Integer order) {
        this.displayOrder = order;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
