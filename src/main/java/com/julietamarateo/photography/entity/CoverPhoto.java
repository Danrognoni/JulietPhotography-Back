package com.julietamarateo.photography.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cover_photos")
public class CoverPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64)
    private String photoId;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    private String title;

    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime updatedAt = LocalDateTime.now();

    public CoverPhoto() {
    }

    public CoverPhoto(String photoId, String imageUrl, String title, String category, String description) {
        this.photoId = photoId;
        this.imageUrl = imageUrl;
        this.title = title;
        this.category = category;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
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
