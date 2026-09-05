package com.julietamarateo.photography.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "album_photos", indexes = {
    @Index(name = "idx_album_photos_album_id", columnList = "album_id"),
    @Index(name = "idx_album_photos_order", columnList = "display_order")
})
public class AlbumPhoto {

    @Id
    @Column(nullable = false, length = 64)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    @JsonIgnore
    private Album album;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    private String caption;

    private String orientation = "portrait"; // portrait, landscape, square

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    private Double x;

    private Double y;

    private Double width;

    private Double height;

    @Column(name = "z_index")
    private Integer zIndex = 1;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public AlbumPhoto() {
    }

    public AlbumPhoto(String id, Album album, String imageUrl, String caption, String orientation, Integer displayOrder) {
        this.id = id;
        this.album = album;
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.orientation = orientation != null ? orientation : "portrait";
        this.displayOrder = displayOrder != null ? displayOrder : 0;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
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
}
