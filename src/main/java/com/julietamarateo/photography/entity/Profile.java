package com.julietamarateo.photography.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "profile")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String title;

    private String location;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String whatsapp;

    private String email;

    private String instagram;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "profile_tags", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    private LocalDateTime updatedAt = LocalDateTime.now();

    public Profile() {
    }

    public Profile(String name, String title, String location, String imageUrl,
                   String bio, String whatsapp, String email, String instagram) {
        this(name, title, location, imageUrl, bio, whatsapp, email, instagram, new ArrayList<>());
    }

    public Profile(String name, String title, String location, String imageUrl,
                   String bio, String whatsapp, String email, String instagram, List<String> tags) {
        this.name = name;
        this.title = title;
        this.location = location;
        this.imageUrl = imageUrl;
        this.bio = bio;
        this.whatsapp = whatsapp;
        this.email = email;
        this.instagram = instagram;
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        } else {
            this.tags.clear();
        }
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
