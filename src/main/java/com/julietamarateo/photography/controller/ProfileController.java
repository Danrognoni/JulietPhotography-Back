package com.julietamarateo.photography.controller;

import com.julietamarateo.photography.dto.ProfileDto;
import com.julietamarateo.photography.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping({"/api/profile", "/profile", "/api/about", "/about"})
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * Endpoint público para consultar la biografía y datos de contacto del perfil.
     */
    @GetMapping
    public ResponseEntity<ProfileDto> getProfile() {
        ProfileDto profile = profileService.getProfile();
        return ResponseEntity.ok(profile);
    }

    /**
     * Endpoint protegido para actualizar los datos de perfil y contacto (JSON).
     */
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfileDto> updateProfile(@Valid @RequestBody ProfileDto dto) {
        ProfileDto updated = profileService.updateProfile(dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Endpoint protegido para actualizar parcialmente los datos de perfil y contacto (PATCH).
     */
    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfileDto> patchProfile(@RequestBody ProfileDto dto) {
        ProfileDto updated = profileService.updateProfile(dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Endpoint protegido para guardar datos de perfil y contacto (POST).
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfileDto> saveProfilePost(@Valid @RequestBody ProfileDto dto) {
        ProfileDto updated = profileService.updateProfile(dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Endpoint protegido para actualizar los datos de perfil y opcionalmente imagen (Multipart).
     */
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfileDto> updateProfileMultipart(
            @ModelAttribute ProfileDto dto,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            ProfileDto imgDto = profileService.updateProfileImage(file);
            dto.setImageUrl(imgDto.getImageUrl());
        }
        ProfileDto updated = profileService.updateProfile(dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Endpoint protegido para subir y actualizar la foto de perfil físicamente.
     */
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfileDto> updateProfileImage(@RequestParam("file") MultipartFile file) {
        ProfileDto updated = profileService.updateProfileImage(file);
        return ResponseEntity.ok(updated);
    }
}
