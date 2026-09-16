package com.julietamarateo.photography.service;

import com.julietamarateo.photography.dto.PhysicalStoreConfigDto;
import com.julietamarateo.photography.entity.PhysicalStoreConfig;
import com.julietamarateo.photography.repository.PhysicalStoreConfigRepository;
import com.julietamarateo.photography.repository.SiteContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
public class PhysicalStoreService {

    private final PhysicalStoreConfigRepository configRepository;
    private final SiteContentRepository siteContentRepository;
    private final FileStorageService fileStorageService;

    public PhysicalStoreService(PhysicalStoreConfigRepository configRepository,
                                SiteContentRepository siteContentRepository,
                                FileStorageService fileStorageService) {
        this.configRepository = configRepository;
        this.siteContentRepository = siteContentRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public PhysicalStoreConfigDto getConfig() {
        PhysicalStoreConfig config = configRepository.findTopByOrderByIdAsc()
                .orElseGet(this::createDefaultConfig);
        return PhysicalStoreConfigDto.fromEntity(config);
    }

    @Transactional
    public PhysicalStoreConfigDto updateConfig(PhysicalStoreConfigDto dto) {
        PhysicalStoreConfig config = configRepository.findTopByOrderByIdAsc()
                .orElseGet(this::createDefaultConfig);

        dto.applyToEntity(config);
        config.setUpdatedAt(LocalDateTime.now());
        PhysicalStoreConfig saved = configRepository.save(config);
        return PhysicalStoreConfigDto.fromEntity(saved);
    }

    @Transactional
    public String uploadBackgroundImage(MultipartFile file) {
        String url = fileStorageService.storeFile(file, "physical-store");
        PhysicalStoreConfig config = configRepository.findTopByOrderByIdAsc()
                .orElseGet(this::createDefaultConfig);

        config.setBgType("image");
        config.setBgValue(url);
        config.setUpdatedAt(LocalDateTime.now());
        configRepository.save(config);
        return url;
    }

    private PhysicalStoreConfig createDefaultConfig() {
        String defaultWhatsapp = "5491136458920";
        try {
            var siteContent = siteContentRepository.findTopByOrderByIdAsc();
            if (siteContent.isPresent() && siteContent.get().getWhatsappNumber() != null && !siteContent.get().getWhatsappNumber().isBlank()) {
                defaultWhatsapp = siteContent.get().getWhatsappNumber().replaceAll("[^0-9]", "");
            }
        } catch (Exception ignored) {
        }

        PhysicalStoreConfig defaultEntity = new PhysicalStoreConfig(
                "Fotos Físicas de Colección",
                "Obras seleccionadas de autor impresas en papeles Fine Art de máxima fidelidad y enmarcados de conservación. Piezas listas para vestir tus espacios.",
                "color",
                "#faf9f6",
                defaultWhatsapp,
                true
        );
        return configRepository.save(defaultEntity);
    }
}
