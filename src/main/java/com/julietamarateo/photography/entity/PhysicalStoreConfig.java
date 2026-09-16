package com.julietamarateo.photography.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "physical_store_config")
public class PhysicalStoreConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sectionTitle;

    @Column(columnDefinition = "TEXT")
    private String sectionSubtitle;

    @Column(length = 20)
    private String bgType = "color"; // 'color', 'gradient', 'image'

    @Column(columnDefinition = "TEXT")
    private String bgValue = "#faf9f6";

    private String whatsappNumber;

    @Column(nullable = false)
    private Boolean isVisible = true;

    private LocalDateTime updatedAt = LocalDateTime.now();

    public PhysicalStoreConfig() {
    }

    public PhysicalStoreConfig(String sectionTitle, String sectionSubtitle, String bgType, String bgValue, String whatsappNumber, Boolean isVisible) {
        this.sectionTitle = sectionTitle;
        this.sectionSubtitle = sectionSubtitle;
        this.bgType = bgType != null ? bgType : "color";
        this.bgValue = bgValue != null ? bgValue : "#faf9f6";
        this.whatsappNumber = whatsappNumber;
        this.isVisible = isVisible != null ? isVisible : true;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public String getSectionSubtitle() {
        return sectionSubtitle;
    }

    public void setSectionSubtitle(String sectionSubtitle) {
        this.sectionSubtitle = sectionSubtitle;
    }

    public String getBgType() {
        return bgType != null ? bgType : "color";
    }

    public void setBgType(String bgType) {
        this.bgType = bgType;
    }

    public String getBgValue() {
        return bgValue != null ? bgValue : "#faf9f6";
    }

    public void setBgValue(String bgValue) {
        this.bgValue = bgValue;
    }

    public String getWhatsappNumber() {
        return whatsappNumber;
    }

    public void setWhatsappNumber(String whatsappNumber) {
        this.whatsappNumber = whatsappNumber;
    }

    public Boolean getIsVisible() {
        return isVisible != null ? isVisible : true;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
