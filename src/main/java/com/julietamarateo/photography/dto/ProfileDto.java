package com.julietamarateo.photography.dto;

import com.julietamarateo.photography.entity.Profile;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

public class ProfileDto {

    private String name;

    private String title;

    private String location;

    private String imageUrl;

    private String bio;

    private String whatsapp;

    @Email(message = "El formato de email no es válido")
    private String email;

    private String instagram;

    private List<String> tags = new ArrayList<>();

    public ProfileDto() {
    }

    public static ProfileDto fromEntity(Profile entity) {
        if (entity == null) return null;
        ProfileDto dto = new ProfileDto();
        dto.setName(entity.getName());
        dto.setTitle(entity.getTitle());
        dto.setLocation(entity.getLocation());
        dto.setImageUrl(entity.getImageUrl());
        dto.setBio(entity.getBio());
        dto.setWhatsapp(entity.getWhatsapp());
        dto.setEmail(entity.getEmail());
        dto.setInstagram(entity.getInstagram());
        if (entity.getTags() != null) {
            dto.setTags(new ArrayList<>(entity.getTags()));
        }
        return dto;
    }

    public void applyToEntity(Profile entity) {
        if (entity == null) return;
        if (this.name != null && !this.name.isBlank()) {
            entity.setName(this.name.trim());
        }
        if (this.title != null) {
            entity.setTitle(this.title.trim());
        }
        if (this.location != null) {
            entity.setLocation(this.location.trim());
        }
        if (this.imageUrl != null && !this.imageUrl.isBlank()) {
            entity.setImageUrl(this.imageUrl.trim());
        }
        if (this.bio != null) {
            entity.setBio(this.bio.trim());
        }
        if (this.whatsapp != null) {
            entity.setWhatsapp(this.whatsapp.trim());
        }
        if (this.email != null) {
            entity.setEmail(this.email.trim());
        }
        if (this.instagram != null) {
            entity.setInstagram(this.instagram.trim());
        }
        if (this.tags != null) {
            entity.setTags(new ArrayList<>(this.tags));
        }
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
        if (tags != null && tags.size() == 1 && tags.get(0) != null && tags.get(0).trim().startsWith("[")) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                this.tags = mapper.readValue(tags.get(0).trim(), new TypeReference<List<String>>() {});
                return;
            } catch (Exception ignored) {
            }
        }
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
    }
}
