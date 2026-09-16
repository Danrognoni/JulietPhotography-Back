package com.julietamarateo.photography.dto;

import com.julietamarateo.photography.entity.PhysicalStoreConfig;

public class PhysicalStoreConfigDto {

    private String sectionTitle;
    private String sectionSubtitle;
    private BackgroundStyleDto backgroundStyle;
    private String whatsappNumber;
    private Boolean isVisible = true;

    public PhysicalStoreConfigDto() {
        this.backgroundStyle = new BackgroundStyleDto("color", "#faf9f6");
    }

    public PhysicalStoreConfigDto(String sectionTitle, String sectionSubtitle, BackgroundStyleDto backgroundStyle, String whatsappNumber, Boolean isVisible) {
        this.sectionTitle = sectionTitle;
        this.sectionSubtitle = sectionSubtitle;
        this.backgroundStyle = backgroundStyle != null ? backgroundStyle : new BackgroundStyleDto("color", "#faf9f6");
        this.whatsappNumber = whatsappNumber;
        this.isVisible = isVisible != null ? isVisible : true;
    }

    public static PhysicalStoreConfigDto fromEntity(PhysicalStoreConfig entity) {
        if (entity == null) return null;
        PhysicalStoreConfigDto dto = new PhysicalStoreConfigDto();
        dto.setSectionTitle(entity.getSectionTitle());
        dto.setSectionSubtitle(entity.getSectionSubtitle());
        dto.setBackgroundStyle(new BackgroundStyleDto(
                entity.getBgType() != null ? entity.getBgType() : "color",
                entity.getBgValue() != null ? entity.getBgValue() : "#faf9f6"
        ));
        dto.setWhatsappNumber(entity.getWhatsappNumber());
        dto.setIsVisible(entity.getIsVisible() != null ? entity.getIsVisible() : true);
        return dto;
    }

    public void applyToEntity(PhysicalStoreConfig entity) {
        if (entity == null) return;
        if (this.sectionTitle != null) entity.setSectionTitle(this.sectionTitle);
        if (this.sectionSubtitle != null) entity.setSectionSubtitle(this.sectionSubtitle);
        if (this.backgroundStyle != null) {
            if (this.backgroundStyle.getType() != null) entity.setBgType(this.backgroundStyle.getType());
            if (this.backgroundStyle.getValue() != null) entity.setBgValue(this.backgroundStyle.getValue());
        }
        if (this.whatsappNumber != null) entity.setWhatsappNumber(this.whatsappNumber);
        if (this.isVisible != null) entity.setIsVisible(this.isVisible);
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

    public BackgroundStyleDto getBackgroundStyle() {
        return backgroundStyle;
    }

    public void setBackgroundStyle(BackgroundStyleDto backgroundStyle) {
        this.backgroundStyle = backgroundStyle;
    }

    public String getWhatsappNumber() {
        return whatsappNumber;
    }

    public void setWhatsappNumber(String whatsappNumber) {
        this.whatsappNumber = whatsappNumber;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public static class BackgroundStyleDto {
        private String type; // 'color' | 'gradient' | 'image'
        private String value;

        public BackgroundStyleDto() {
        }

        public BackgroundStyleDto(String type, String value) {
            this.type = type;
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
