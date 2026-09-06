package com.julietamarateo.photography.service;

import com.julietamarateo.photography.dto.SiteContentDto;
import com.julietamarateo.photography.entity.Profile;
import com.julietamarateo.photography.entity.SiteContent;
import com.julietamarateo.photography.repository.ProfileRepository;
import com.julietamarateo.photography.repository.SiteContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
public class SiteContentService {

    private final SiteContentRepository siteContentRepository;
    private final ProfileRepository profileRepository;
    private final FileStorageService fileStorageService;

    public SiteContentService(SiteContentRepository siteContentRepository,
                              ProfileRepository profileRepository,
                              FileStorageService fileStorageService) {
        this.siteContentRepository = siteContentRepository;
        this.profileRepository = profileRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public SiteContentDto getSiteContent() {
        SiteContent entity = siteContentRepository.findTopByOrderByIdAsc()
                .orElseGet(this::createDefaultSiteContent);
        return SiteContentDto.fromEntity(entity);
    }

    @Transactional
    public SiteContentDto updateSiteContent(SiteContentDto dto) {
        SiteContent entity = siteContentRepository.findTopByOrderByIdAsc()
                .orElseGet(this::createDefaultSiteContent);

        dto.applyToEntity(entity);
        entity.setUpdatedAt(LocalDateTime.now());
        SiteContent saved = siteContentRepository.save(entity);

        // Sincronizar campos compartidos con Profile para mantener consistencia
        profileRepository.findTopByOrderByIdAsc().ifPresent(profile -> {
            if (dto.getBrandName() != null) profile.setName(dto.getBrandName());
            if (dto.getAboutSubtitle() != null) profile.setTitle(dto.getAboutSubtitle());
            if (dto.getContactLocation() != null) profile.setLocation(dto.getContactLocation());
            if (dto.getAboutBio() != null) profile.setBio(dto.getAboutBio());
            if (dto.getAboutImageUrl() != null) profile.setImageUrl(dto.getAboutImageUrl());
            if (dto.getContactEmail() != null) profile.setEmail(dto.getContactEmail());
            if (dto.getWhatsappNumber() != null) profile.setWhatsapp(dto.getWhatsappNumber());
            if (dto.getInstagramHandle() != null) profile.setInstagram(dto.getInstagramHandle());
            profile.setUpdatedAt(LocalDateTime.now());
            profileRepository.save(profile);
        });

        return SiteContentDto.fromEntity(saved);
    }

    @Transactional
    public String uploadSiteImage(MultipartFile file, String targetField) {
        String url = fileStorageService.storeFile(file, "site");

        SiteContent entity = siteContentRepository.findTopByOrderByIdAsc()
                .orElseGet(this::createDefaultSiteContent);

        if (targetField != null) {
            switch (targetField.toLowerCase()) {
                case "hero":
                case "herobg":
                    entity.setHeroBgUrl(url);
                    break;
                case "vignettes1":
                    entity.setVignettesImage1(url);
                    break;
                case "vignettes2":
                    entity.setVignettesImage2(url);
                    break;
                case "storybg":
                    entity.setStoryBgUrl(url);
                    break;
                case "portrait":
                case "storyportrait":
                    entity.setStoryPortraitUrl(url);
                    break;
                case "about":
                case "profile":
                    entity.setAboutImageUrl(url);
                    profileRepository.findTopByOrderByIdAsc().ifPresent(p -> {
                        p.setImageUrl(url);
                        profileRepository.save(p);
                    });
                    break;
                default:
                    break;
            }
        }

        entity.setUpdatedAt(LocalDateTime.now());
        siteContentRepository.save(entity);
        return url;
    }

    public SiteContent createDefaultSiteContent() {
        SiteContent sc = new SiteContent();
        sc.setBrandName("JulietaMarateo");
        sc.setBrandTagline("Fotografía Profesional & Documental");
        sc.setHeroTitle("The World, Unfiltered");
        sc.setHeroSubtitle("Journeys captured beyond the postcard view");
        sc.setHeroButtonText("Explore Projects");
        sc.setHeroBgUrl("https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=2000&q=85");

        sc.setMenuHome("Home");
        sc.setMenuPortfolio("Portfolio");
        sc.setMenuAbout("About");
        sc.setMenuContact("Contact");

        sc.setVignettesKicker("Vignettes from the edge");
        sc.setVignettesTitle("A curated selection of recent expeditions and untold stories");
        sc.setVignettesLabel1("Tokyo's Neon Pulse");
        sc.setVignettesImage1("https://images.unsplash.com/photo-1503899036084-c55cdd92da26?auto=format&fit=crop&w=1200&q=85");
        sc.setVignettesImage2("https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?auto=format&fit=crop&w=1200&q=85");

        sc.setStoryKickerLeft("Beyond the frame");
        sc.setStoryKickerRight("Stories in motion");
        sc.setStoryButtonText("My Story");
        sc.setStoryBgUrl("https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?auto=format&fit=crop&w=2000&q=85");
        sc.setStoryPortraitUrl("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=800&q=85");

        sc.setAboutTitle("JulietaMarateo");
        sc.setAboutSubtitle("Fotógrafa Profesional & Documental");
        sc.setAboutBio("JulietaMarateo es una fotógrafa profesional enfocada en capturar momentos únicos, emociones reales e historias visuales con una perspectiva sensible y auténtica.");
        sc.setAboutQuote("Photography is not about documenting places; it's about holding on to the ephemeral light and silent narratives that define who we are.");
        sc.setAboutImageUrl("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=800&q=85");

        sc.setContactTitle("Get in Touch");
        sc.setContactSubtitle("Available for worldwide expeditions, editorial assignments and fine art print commissions.");
        sc.setContactEmail("contacto@julietamarateo.com");
        sc.setContactPhone("+1 (555) 349-2810");
        sc.setContactLocation("Tokyo · Patagonia · Worldwide");
        sc.setInstagramHandle("@julietamarateo");
        sc.setWhatsappNumber("+15553492810");

        sc.setFooterText("Journeys captured beyond the postcard view. All images shot on location worldwide.");
        sc.setCopyrightText("© 2026 JulietaMarateo. Todos los derechos reservados.");
        sc.setUpdatedAt(LocalDateTime.now());

        return siteContentRepository.save(sc);
    }
}
