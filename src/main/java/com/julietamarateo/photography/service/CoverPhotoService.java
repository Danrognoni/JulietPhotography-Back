package com.julietamarateo.photography.service;

import com.julietamarateo.photography.dto.CoverPhotoDto;
import com.julietamarateo.photography.entity.CoverPhoto;
import com.julietamarateo.photography.entity.Photo;
import com.julietamarateo.photography.repository.CoverPhotoRepository;
import com.julietamarateo.photography.repository.PhotoRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
public class CoverPhotoService {

    private final CoverPhotoRepository coverPhotoRepository;
    private final PhotoRepository photoRepository;
    private final FileStorageService fileStorageService;

    public CoverPhotoService(CoverPhotoRepository coverPhotoRepository,
                             PhotoRepository photoRepository,
                             FileStorageService fileStorageService) {
        this.coverPhotoRepository = coverPhotoRepository;
        this.photoRepository = photoRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "coverPhoto", key = "'current'")
    public CoverPhotoDto getCoverPhoto() {
        CoverPhoto cover = coverPhotoRepository.findTopByOrderByUpdatedAtDesc()
                .orElseGet(this::createDefaultCoverPhoto);
        return CoverPhotoDto.fromEntity(cover);
    }

    @Transactional
    @CacheEvict(value = "coverPhoto", allEntries = true)
    public CoverPhotoDto updateCoverPhoto(CoverPhotoDto dto, MultipartFile file) {
        CoverPhoto cover = coverPhotoRepository.findTopByOrderByUpdatedAtDesc()
                .orElseGet(this::createDefaultCoverPhoto);

        if (dto != null && dto.getPhotoId() != null && !dto.getPhotoId().isBlank()) {
            photoRepository.findById(dto.getPhotoId()).ifPresent(photo -> {
                cover.setPhotoId(photo.getId());
                if (dto.getImageUrl() == null || dto.getImageUrl().isBlank()) {
                    cover.setImageUrl(photo.getImageUrl());
                }
                if (dto.getTitle() == null || dto.getTitle().isBlank()) {
                    cover.setTitle(photo.getTitle());
                }
                if (dto.getCategory() == null || dto.getCategory().isBlank()) {
                    cover.setCategory(photo.getCategory());
                }
                if (dto.getDescription() == null || dto.getDescription().isBlank()) {
                    cover.setDescription(photo.getDescription());
                }
            });
        }

        if (dto != null) {
            dto.applyToEntity(cover);
        }

        if (file != null && !file.isEmpty()) {
            fileStorageService.deleteFile(cover.getImageUrl());
            String uploadedUrl = fileStorageService.storeFile(file, "cover");
            cover.setImageUrl(uploadedUrl);
        }

        cover.setUpdatedAt(LocalDateTime.now());
        CoverPhoto saved = coverPhotoRepository.save(cover);
        return CoverPhotoDto.fromEntity(saved);
    }

    private CoverPhoto createDefaultCoverPhoto() {
        CoverPhoto defaultCover = new CoverPhoto(
                "photo-1",
                "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1200&q=85",
                "Amanecer en los Acantilados",
                "Paisajismo",
                "Luz dorada matutina sobre la costa marítima de Mar del Plata."
        );
        return coverPhotoRepository.save(defaultCover);
    }
}
