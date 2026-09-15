package com.julietamarateo.photography.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "site_content")
public class SiteContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brandName;
    private String brandTagline;

    @Column(columnDefinition = "TEXT")
    private String heroTitle;

    @Column(columnDefinition = "TEXT")
    private String heroSubtitle;

    private String heroButtonText;

    @Column(columnDefinition = "TEXT")
    private String heroBgUrl;

    private String menuHome;
    private String menuPortfolio;
    private String menuAbout;
    private String menuContact;

    private String vignettesKicker;

    @Column(columnDefinition = "TEXT")
    private String vignettesTitle;

    private String vignettesLabel1;

    @Column(columnDefinition = "TEXT")
    private String vignettesImage1;

    @Column(columnDefinition = "TEXT")
    private String vignettesImage2;

    private String storyKickerLeft;
    private String storyKickerRight;
    private String storyButtonText;

    @Column(columnDefinition = "TEXT")
    private String storyBgUrl;

    @Column(columnDefinition = "TEXT")
    private String storyPortraitUrl;

    private String aboutTitle;
    private String aboutSubtitle;

    @Column(columnDefinition = "TEXT")
    private String aboutBio;

    @Column(columnDefinition = "TEXT")
    private String aboutQuote;

    @Column(columnDefinition = "TEXT")
    private String aboutImageUrl;

    private String contactTitle;

    @Column(columnDefinition = "TEXT")
    private String contactSubtitle;

    private String contactEmail;
    private String contactPhone;
    private String contactLocation;
    private String instagramHandle;
    private String whatsappNumber;

    @Column(columnDefinition = "TEXT")
    private String footerText;

    private String copyrightText;

    @Column(columnDefinition = "TEXT")
    private String portfolioTitle;

    @Column(name = "portfolio_bg_color", length = 32)
    private String portfolioBgColor;

    @Column(name = "global_font", length = 64)
    private String globalFont;

    @Column(name = "sobre_mi_bg_color", length = 32)
    private String sobreMiBgColor;

    @Column(name = "faq_bg_color", length = 32)
    private String faqBgColor;

    @Column(name = "contacto_bg_color", length = 32)
    private String contactoBgColor;

    @Column(name = "is_faq_visible")
    private Boolean isFaqVisible = true;

    @Column(name = "is_sobre_mi_visible")
    private Boolean isSobreMiVisible = true;

    @Column(name = "is_contacto_visible")
    private Boolean isContactoVisible = true;

    private LocalDateTime updatedAt = LocalDateTime.now();

    public SiteContent() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandTagline() {
        return brandTagline;
    }

    public void setBrandTagline(String brandTagline) {
        this.brandTagline = brandTagline;
    }

    public String getHeroTitle() {
        return heroTitle;
    }

    public void setHeroTitle(String heroTitle) {
        this.heroTitle = heroTitle;
    }

    public String getHeroSubtitle() {
        return heroSubtitle;
    }

    public void setHeroSubtitle(String heroSubtitle) {
        this.heroSubtitle = heroSubtitle;
    }

    public String getHeroButtonText() {
        return heroButtonText;
    }

    public void setHeroButtonText(String heroButtonText) {
        this.heroButtonText = heroButtonText;
    }

    public String getHeroBgUrl() {
        return heroBgUrl;
    }

    public void setHeroBgUrl(String heroBgUrl) {
        this.heroBgUrl = heroBgUrl;
    }

    public String getMenuHome() {
        return menuHome;
    }

    public void setMenuHome(String menuHome) {
        this.menuHome = menuHome;
    }

    public String getMenuPortfolio() {
        return menuPortfolio;
    }

    public void setMenuPortfolio(String menuPortfolio) {
        this.menuPortfolio = menuPortfolio;
    }

    public String getMenuAbout() {
        return menuAbout;
    }

    public void setMenuAbout(String menuAbout) {
        this.menuAbout = menuAbout;
    }

    public String getMenuContact() {
        return menuContact;
    }

    public void setMenuContact(String menuContact) {
        this.menuContact = menuContact;
    }

    public String getVignettesKicker() {
        return vignettesKicker;
    }

    public void setVignettesKicker(String vignettesKicker) {
        this.vignettesKicker = vignettesKicker;
    }

    public String getVignettesTitle() {
        return vignettesTitle;
    }

    public void setVignettesTitle(String vignettesTitle) {
        this.vignettesTitle = vignettesTitle;
    }

    public String getVignettesLabel1() {
        return vignettesLabel1;
    }

    public void setVignettesLabel1(String vignettesLabel1) {
        this.vignettesLabel1 = vignettesLabel1;
    }

    public String getVignettesImage1() {
        return vignettesImage1;
    }

    public void setVignettesImage1(String vignettesImage1) {
        this.vignettesImage1 = vignettesImage1;
    }

    public String getVignettesImage2() {
        return vignettesImage2;
    }

    public void setVignettesImage2(String vignettesImage2) {
        this.vignettesImage2 = vignettesImage2;
    }

    public String getStoryKickerLeft() {
        return storyKickerLeft;
    }

    public void setStoryKickerLeft(String storyKickerLeft) {
        this.storyKickerLeft = storyKickerLeft;
    }

    public String getStoryKickerRight() {
        return storyKickerRight;
    }

    public void setStoryKickerRight(String storyKickerRight) {
        this.storyKickerRight = storyKickerRight;
    }

    public String getStoryButtonText() {
        return storyButtonText;
    }

    public void setStoryButtonText(String storyButtonText) {
        this.storyButtonText = storyButtonText;
    }

    public String getStoryBgUrl() {
        return storyBgUrl;
    }

    public void setStoryBgUrl(String storyBgUrl) {
        this.storyBgUrl = storyBgUrl;
    }

    public String getStoryPortraitUrl() {
        return storyPortraitUrl;
    }

    public void setStoryPortraitUrl(String storyPortraitUrl) {
        this.storyPortraitUrl = storyPortraitUrl;
    }

    public String getAboutTitle() {
        return aboutTitle;
    }

    public void setAboutTitle(String aboutTitle) {
        this.aboutTitle = aboutTitle;
    }

    public String getAboutSubtitle() {
        return aboutSubtitle;
    }

    public void setAboutSubtitle(String aboutSubtitle) {
        this.aboutSubtitle = aboutSubtitle;
    }

    public String getAboutBio() {
        return aboutBio;
    }

    public void setAboutBio(String aboutBio) {
        this.aboutBio = aboutBio;
    }

    public String getAboutQuote() {
        return aboutQuote;
    }

    public void setAboutQuote(String aboutQuote) {
        this.aboutQuote = aboutQuote;
    }

    public String getAboutImageUrl() {
        return aboutImageUrl;
    }

    public void setAboutImageUrl(String aboutImageUrl) {
        this.aboutImageUrl = aboutImageUrl;
    }

    public String getContactTitle() {
        return contactTitle;
    }

    public void setContactTitle(String contactTitle) {
        this.contactTitle = contactTitle;
    }

    public String getContactSubtitle() {
        return contactSubtitle;
    }

    public void setContactSubtitle(String contactSubtitle) {
        this.contactSubtitle = contactSubtitle;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactLocation() {
        return contactLocation;
    }

    public void setContactLocation(String contactLocation) {
        this.contactLocation = contactLocation;
    }

    public String getInstagramHandle() {
        return instagramHandle;
    }

    public void setInstagramHandle(String instagramHandle) {
        this.instagramHandle = instagramHandle;
    }

    public String getWhatsappNumber() {
        return whatsappNumber;
    }

    public void setWhatsappNumber(String whatsappNumber) {
        this.whatsappNumber = whatsappNumber;
    }

    public String getFooterText() {
        return footerText;
    }

    public void setFooterText(String footerText) {
        this.footerText = footerText;
    }

    public String getCopyrightText() {
        return copyrightText;
    }

    public void setCopyrightText(String copyrightText) {
        this.copyrightText = copyrightText;
    }

    public String getPortfolioTitle() {
        return portfolioTitle;
    }

    public void setPortfolioTitle(String portfolioTitle) {
        this.portfolioTitle = portfolioTitle;
    }

    public String getPortfolioBgColor() {
        return portfolioBgColor;
    }

    public void setPortfolioBgColor(String portfolioBgColor) {
        this.portfolioBgColor = portfolioBgColor;
    }

    public String getGlobalFont() {
        return globalFont;
    }

    public void setGlobalFont(String globalFont) {
        this.globalFont = globalFont;
    }

    public String getSobreMiBgColor() {
        return sobreMiBgColor;
    }

    public void setSobreMiBgColor(String sobreMiBgColor) {
        this.sobreMiBgColor = sobreMiBgColor;
    }

    public String getFaqBgColor() {
        return faqBgColor;
    }

    public void setFaqBgColor(String faqBgColor) {
        this.faqBgColor = faqBgColor;
    }

    public String getContactoBgColor() {
        return contactoBgColor;
    }

    public void setContactoBgColor(String contactoBgColor) {
        this.contactoBgColor = contactoBgColor;
    }

    public Boolean getIsFaqVisible() {
        return isFaqVisible != null ? isFaqVisible : true;
    }

    public void setIsFaqVisible(Boolean isFaqVisible) {
        this.isFaqVisible = isFaqVisible;
    }

    public Boolean getIsSobreMiVisible() {
        return isSobreMiVisible != null ? isSobreMiVisible : true;
    }

    public void setIsSobreMiVisible(Boolean isSobreMiVisible) {
        this.isSobreMiVisible = isSobreMiVisible;
    }

    public Boolean getIsContactoVisible() {
        return isContactoVisible != null ? isContactoVisible : true;
    }

    public void setIsContactoVisible(Boolean isContactoVisible) {
        this.isContactoVisible = isContactoVisible;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
