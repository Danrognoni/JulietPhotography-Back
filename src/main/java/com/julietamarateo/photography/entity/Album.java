package com.julietamarateo.photography.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "albums", indexes = {
    @Index(name = "idx_albums_name", columnList = "name"),
    @Index(name = "idx_albums_order", columnList = "display_order")
})
public class Album {

    @Id
    @Column(nullable = false, length = 64)
    private String id;

    @Column(nullable = false)
    private String name;

    private String subtitle;

    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String coverImage;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "x_pos")
    private Double xPos;

    @Column(name = "y_pos")
    private Double yPos;

    @Column(name = "width")
    private Double width;

    @Column(name = "z_index")
    private Integer zIndex = 1;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC, createdAt ASC")
    private List<AlbumPhoto> photos = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "album_photo_urls", joinColumns = @JoinColumn(name = "album_id"))
    @Column(name = "photo_url")
    private List<String> photoUrls = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    public Album() {
    }

    public Album(String id, String name, String category, String description, String coverImage, Integer displayOrder) {
        this.id = id;
        this.name = name;
        this.category = category != null ? category : name;
        this.description = description;
        this.coverImage = coverImage;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
        this.photoUrls = new ArrayList<>();
        this.photos = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Album(String id, String title, String subtitle, String description, String coverImageUrl, Integer displayOrder, boolean unused) {
        this.id = id;
        this.name = title;
        this.subtitle = subtitle;
        this.category = title;
        this.description = description;
        this.coverImage = coverImageUrl;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
        this.photoUrls = new ArrayList<>();
        this.photos = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    public String getTitle() {
        return name;
    }

    public void setTitle(String title) {
        this.name = title;
        if (this.category == null || this.category.isBlank()) {
            this.category = title;
        }
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
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getCoverImageUrl() {
        return coverImage;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImage = coverImageUrl;
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

    public List<AlbumPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<AlbumPhoto> photos) {
        this.photos = photos != null ? photos : new ArrayList<>();
    }

    public void addPhoto(AlbumPhoto photo) {
        if (photos == null) photos = new ArrayList<>();
        photos.add(photo);
        photo.setAlbum(this);
    }

    public void removePhoto(AlbumPhoto photo) {
        if (photos != null) {
            photos.remove(photo);
            photo.setAlbum(null);
        }
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls != null ? photoUrls : new ArrayList<>();
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

    public Double getXPos() {
        return xPos;
    }

    public void setXPos(Double xPos) {
        this.xPos = xPos;
    }

    public Double getYPos() {
        return yPos;
    }

    public void setYPos(Double yPos) {
        this.yPos = yPos;
    }

    public Double getX() {
        return xPos;
    }

    public void setX(Double x) {
        this.xPos = x;
    }

    public Double getY() {
        return yPos;
    }

    public void setY(Double y) {
        this.yPos = y;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Integer getZIndex() {
        return zIndex;
    }

    public void setZIndex(Integer zIndex) {
        this.zIndex = zIndex;
    }
}
