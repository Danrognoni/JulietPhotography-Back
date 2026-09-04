package com.julietamarateo.photography.service;

import com.julietamarateo.photography.dto.ProfileDto;
import com.julietamarateo.photography.entity.Profile;
import com.julietamarateo.photography.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final FileStorageService fileStorageService;

    public ProfileService(ProfileRepository profileRepository, FileStorageService fileStorageService) {
        this.profileRepository = profileRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public ProfileDto getProfile() {
        Profile profile = profileRepository.findTopByOrderByIdAsc()
                .orElseGet(this::createDefaultProfile);
        return ProfileDto.fromEntity(profile);
    }

    @Transactional
    public ProfileDto updateProfile(ProfileDto dto) {
        Profile profile = profileRepository.findTopByOrderByIdAsc()
                .orElseGet(this::createDefaultProfile);

        dto.applyToEntity(profile);
        profile.setUpdatedAt(LocalDateTime.now());

        Profile saved = profileRepository.save(profile);
        return ProfileDto.fromEntity(saved);
    }

    @Transactional
    public ProfileDto updateProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen no puede estar vacío");
        }

        Profile profile = profileRepository.findTopByOrderByIdAsc()
                .orElseGet(this::createDefaultProfile);

        // Borrar imagen anterior si es local
        fileStorageService.deleteFile(profile.getImageUrl());

        String uploadedUrl = fileStorageService.storeFile(file, "profile");
        profile.setImageUrl(uploadedUrl);
        profile.setUpdatedAt(LocalDateTime.now());

        Profile saved = profileRepository.save(profile);
        return ProfileDto.fromEntity(saved);
    }

    private Profile createDefaultProfile() {
        Profile defaultProfile = new Profile(
                "Julieta Marateo",
                "Técnica en Fotografía",
                "Mar del Plata, Argentina",
                "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=800&q=85",
                "Hola, mi nombre es Julieta Marateo. Soy Técnica en Fotografía radicada en Mar del Plata. Me apasiona capturar momentos únicos, encargándome con máxima dedicación tanto de la toma fotográfica como de la postproducción y edición profesional.",
                "2281311917",
                "julietamarateo4@gmail.com",
                "@julietamph_"
        );
        return profileRepository.save(defaultProfile);
    }
}
