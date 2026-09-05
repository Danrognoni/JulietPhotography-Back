package com.julietamarateo.photography.dto;

import com.julietamarateo.photography.entity.SiteContent;

public class SiteContentDto {

    private String brandName;
    private String brandTagline;
    private String heroTitle;
    private String heroSubtitle;
    private String heroButtonText;
    private String heroBgUrl;

    private String menuHome;
    private String menuPortfolio;
    private String menuAbout;
    private String menuContact;

    private String vignettesKicker;
    private String vignettesTitle;
    private String vignettesLabel1;
    private String vignettesImage1;
    private String vignettesImage2;

    private String storyKickerLeft;
    private String storyKickerRight;
    private String storyButtonText;
    private String storyBgUrl;
    private String storyPortraitUrl;

    private String aboutTitle;
    private String aboutSubtitle;
    private String aboutBio;
    private String aboutQuote;
    private String aboutImageUrl;

    private String contactTitle;
    private String contactSubtitle;
    private String contactEmail;
    private String contactPhone;
    private String contactLocation;
    private String instagramHandle;
    private String whatsappNumber;

    private String footerText;
    private String copyrightText;

    public SiteContentDto() {
    }

    public static SiteContentDto fromEntity(SiteContent entity) {
        if (entity == null) return null;
        SiteContentDto dto = new SiteContentDto();
        dto.setBrandName(entity.getBrandName());
        dto.setBrandTagline(entity.getBrandTagline());
        dto.setHeroTitle(entity.getHeroTitle());
        dto.setHeroSubtitle(entity.getHeroSubtitle());
        dto.setHeroButtonText(entity.getHeroButtonText());
        dto.setHeroBgUrl(entity.getHeroBgUrl());

        dto.setMenuHome(entity.getMenuHome());
        dto.setMenuPortfolio(entity.getMenuPortfolio());
        dto.setMenuAbout(entity.getMenuAbout());
        dto.setMenuContact(entity.getMenuContact());

        dto.setVignettesKicker(entity.getVignettesKicker());
        dto.setVignettesTitle(entity.getVignettesTitle());
        dto.setVignettesLabel1(entity.getVignettesLabel1());
        dto.setVignettesImage1(entity.getVignettesImage1());
        dto.setVignettesImage2(entity.getVignettesImage2());

        dto.setStoryKickerLeft(entity.getStoryKickerLeft());
        dto.setStoryKickerRight(entity.getStoryKickerRight());
        dto.setStoryButtonText(entity.getStoryButtonText());
        dto.setStoryBgUrl(entity.getStoryBgUrl());
        dto.setStoryPortraitUrl(entity.getStoryPortraitUrl());

        dto.setAboutTitle(entity.getAboutTitle());
        dto.setAboutSubtitle(entity.getAboutSubtitle());
        dto.setAboutBio(entity.getAboutBio());
        dto.setAboutQuote(entity.getAboutQuote());
        dto.setAboutImageUrl(entity.getAboutImageUrl());

        dto.setContactTitle(entity.getContactTitle());
        dto.setContactSubtitle(entity.getContactSubtitle());
        dto.setContactEmail(entity.getContactEmail());
        dto.setContactPhone(entity.getContactPhone());
        dto.setContactLocation(entity.getContactLocation());
        dto.setInstagramHandle(entity.getInstagramHandle());
        dto.setWhatsappNumber(entity.getWhatsappNumber());

        dto.setFooterText(entity.getFooterText());
        dto.setCopyrightText(entity.getCopyrightText());
        return dto;
    }

    public void applyToEntity(SiteContent entity) {
        if (entity == null) return;
        if (this.brandName != null) entity.setBrandName(this.brandName.trim());
        if (this.brandTagline != null) entity.setBrandTagline(this.brandTagline.trim());
        if (this.heroTitle != null) entity.setHeroTitle(this.heroTitle.trim());
        if (this.heroSubtitle != null) entity.setHeroSubtitle(this.heroSubtitle.trim());
        if (this.heroButtonText != null) entity.setHeroButtonText(this.heroButtonText.trim());
        if (this.heroBgUrl != null) entity.setHeroBgUrl(this.heroBgUrl.trim());

        if (this.menuHome != null) entity.setMenuHome(this.menuHome.trim());
        if (this.menuPortfolio != null) entity.setMenuPortfolio(this.menuPortfolio.trim());
        if (this.menuAbout != null) entity.setMenuAbout(this.menuAbout.trim());
        if (this.menuContact != null) entity.setMenuContact(this.menuContact.trim());

        if (this.vignettesKicker != null) entity.setVignettesKicker(this.vignettesKicker.trim());
        if (this.vignettesTitle != null) entity.setVignettesTitle(this.vignettesTitle.trim());
        if (this.vignettesLabel1 != null) entity.setVignettesLabel1(this.vignettesLabel1.trim());
        if (this.vignettesImage1 != null) entity.setVignettesImage1(this.vignettesImage1.trim());
        if (this.vignettesImage2 != null) entity.setVignettesImage2(this.vignettesImage2.trim());

        if (this.storyKickerLeft != null) entity.setStoryKickerLeft(this.storyKickerLeft.trim());
        if (this.storyKickerRight != null) entity.setStoryKickerRight(this.storyKickerRight.trim());
        if (this.storyButtonText != null) entity.setStoryButtonText(this.storyButtonText.trim());
        if (this.storyBgUrl != null) entity.setStoryBgUrl(this.storyBgUrl.trim());
        if (this.storyPortraitUrl != null) entity.setStoryPortraitUrl(this.storyPortraitUrl.trim());

        if (this.aboutTitle != null) entity.setAboutTitle(this.aboutTitle.trim());
        if (this.aboutSubtitle != null) entity.setAboutSubtitle(this.aboutSubtitle.trim());
        if (this.aboutBio != null) entity.setAboutBio(this.aboutBio.trim());
        if (this.aboutQuote != null) entity.setAboutQuote(this.aboutQuote.trim());
        if (this.aboutImageUrl != null) entity.setAboutImageUrl(this.aboutImageUrl.trim());

        if (this.contactTitle != null) entity.setContactTitle(this.contactTitle.trim());
        if (this.contactSubtitle != null) entity.setContactSubtitle(this.contactSubtitle.trim());
        if (this.contactEmail != null) entity.setContactEmail(this.contactEmail.trim());
        if (this.contactPhone != null) entity.setContactPhone(this.contactPhone.trim());
        if (this.contactLocation != null) entity.setContactLocation(this.contactLocation.trim());
        if (this.instagramHandle != null) entity.setInstagramHandle(this.instagramHandle.trim());
        if (this.whatsappNumber != null) entity.setWhatsappNumber(this.whatsappNumber.trim());

        if (this.footerText != null) entity.setFooterText(this.footerText.trim());
        if (this.copyrightText != null) entity.setCopyrightText(this.copyrightText.trim());
    }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getBrandTagline() { return brandTagline; }
    public void setBrandTagline(String brandTagline) { this.brandTagline = brandTagline; }

    public String getHeroTitle() { return heroTitle; }
    public void setHeroTitle(String heroTitle) { this.heroTitle = heroTitle; }

    public String getHeroSubtitle() { return heroSubtitle; }
    public void setHeroSubtitle(String heroSubtitle) { this.heroSubtitle = heroSubtitle; }

    public String getHeroButtonText() { return heroButtonText; }
    public void setHeroButtonText(String heroButtonText) { this.heroButtonText = heroButtonText; }

    public String getHeroBgUrl() { return heroBgUrl; }
    public void setHeroBgUrl(String heroBgUrl) { this.heroBgUrl = heroBgUrl; }

    public String getMenuHome() { return menuHome; }
    public void setMenuHome(String menuHome) { this.menuHome = menuHome; }

    public String getMenuPortfolio() { return menuPortfolio; }
    public void setMenuPortfolio(String menuPortfolio) { this.menuPortfolio = menuPortfolio; }

    public String getMenuAbout() { return menuAbout; }
    public void setMenuAbout(String menuAbout) { this.menuAbout = menuAbout; }

    public String getMenuContact() { return menuContact; }
    public void setMenuContact(String menuContact) { this.menuContact = menuContact; }

    public String getVignettesKicker() { return vignettesKicker; }
    public void setVignettesKicker(String vignettesKicker) { this.vignettesKicker = vignettesKicker; }

    public String getVignettesTitle() { return vignettesTitle; }
    public void setVignettesTitle(String vignettesTitle) { this.vignettesTitle = vignettesTitle; }

    public String getVignettesLabel1() { return vignettesLabel1; }
    public void setVignettesLabel1(String vignettesLabel1) { this.vignettesLabel1 = vignettesLabel1; }

    public String getVignettesImage1() { return vignettesImage1; }
    public void setVignettesImage1(String vignettesImage1) { this.vignettesImage1 = vignettesImage1; }

    public String getVignettesImage2() { return vignettesImage2; }
    public void setVignettesImage2(String vignettesImage2) { this.vignettesImage2 = vignettesImage2; }

    public String getStoryKickerLeft() { return storyKickerLeft; }
    public void setStoryKickerLeft(String storyKickerLeft) { this.storyKickerLeft = storyKickerLeft; }

    public String getStoryKickerRight() { return storyKickerRight; }
    public void setStoryKickerRight(String storyKickerRight) { this.storyKickerRight = storyKickerRight; }

    public String getStoryButtonText() { return storyButtonText; }
    public void setStoryButtonText(String storyButtonText) { this.storyButtonText = storyButtonText; }

    public String getStoryBgUrl() { return storyBgUrl; }
    public void setStoryBgUrl(String storyBgUrl) { this.storyBgUrl = storyBgUrl; }

    public String getStoryPortraitUrl() { return storyPortraitUrl; }
    public void setStoryPortraitUrl(String storyPortraitUrl) { this.storyPortraitUrl = storyPortraitUrl; }

    public String getAboutTitle() { return aboutTitle; }
    public void setAboutTitle(String aboutTitle) { this.aboutTitle = aboutTitle; }

    public String getAboutSubtitle() { return aboutSubtitle; }
    public void setAboutSubtitle(String aboutSubtitle) { this.aboutSubtitle = aboutSubtitle; }

    public String getAboutBio() { return aboutBio; }
    public void setAboutBio(String aboutBio) { this.aboutBio = aboutBio; }

    public String getAboutQuote() { return aboutQuote; }
    public void setAboutQuote(String aboutQuote) { this.aboutQuote = aboutQuote; }

    public String getAboutImageUrl() { return aboutImageUrl; }
    public void setAboutImageUrl(String aboutImageUrl) { this.aboutImageUrl = aboutImageUrl; }

    public String getContactTitle() { return contactTitle; }
    public void setContactTitle(String contactTitle) { this.contactTitle = contactTitle; }

    public String getContactSubtitle() { return contactSubtitle; }
    public void setContactSubtitle(String contactSubtitle) { this.contactSubtitle = contactSubtitle; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactLocation() { return contactLocation; }
    public void setContactLocation(String contactLocation) { this.contactLocation = contactLocation; }

    public String getInstagramHandle() { return instagramHandle; }
    public void setInstagramHandle(String instagramHandle) { this.instagramHandle = instagramHandle; }

    public String getWhatsappNumber() { return whatsappNumber; }
    public void setWhatsappNumber(String whatsappNumber) { this.whatsappNumber = whatsappNumber; }

    public String getFooterText() { return footerText; }
    public void setFooterText(String footerText) { this.footerText = footerText; }

    public String getCopyrightText() { return copyrightText; }
    public void setCopyrightText(String copyrightText) { this.copyrightText = copyrightText; }
}
