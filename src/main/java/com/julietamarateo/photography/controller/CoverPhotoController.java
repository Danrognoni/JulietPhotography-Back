package com.julietamarateo.photography.controller;

import com.julietamarateo.photography.dto.CoverPhotoDto;
import com.julietamarateo.photography.service.CoverPhotoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping({"/api/cover-photo", "/cover-photo"})
public class CoverPhotoController {

    private final CoverPhotoService coverPhotoService;

    public CoverPhotoController(CoverPhotoService coverPhotoService) {
        this.coverPhotoService = coverPhotoService;
    }

    /**
     * Endpoint público para obtener la foto de portada actual del Hero.
     */
    @GetMapping
    public ResponseEntity<CoverPhotoDto> getCoverPhoto() {
        CoverPhotoDto cover = coverPhotoService.getCoverPhoto();
        return ResponseEntity.ok(cover);
    }

    /**
     * Endpoint protegido para actualizar la foto de portada (JSON).
     */
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CoverPhotoDto> updateCoverPhotoJson(@RequestBody CoverPhotoDto dto) {
        CoverPhotoDto updated = coverPhotoService.updateCoverPhoto(dto, null);
        return ResponseEntity.ok(updated);
    }

    /**
     * Endpoint protegido para actualizar la foto de portada con archivo físico (Multipart).
     */
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CoverPhotoDto> updateCoverPhotoMultipart(
            @ModelAttribute CoverPhotoDto dto,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        CoverPhotoDto updated = coverPhotoService.updateCoverPhoto(dto, file);
        return ResponseEntity.ok(updated);
    }

    /**
     * Endpoint alternativo POST protegido para actualizar la foto de portada (JSON).
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CoverPhotoDto> postCoverPhotoJson(@RequestBody CoverPhotoDto dto) {
        CoverPhotoDto updated = coverPhotoService.updateCoverPhoto(dto, null);
        return ResponseEntity.ok(updated);
    }

    /**
     * Endpoint alternativo POST protegido para actualizar la foto de portada con archivo físico (Multipart).
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CoverPhotoDto> postCoverPhotoMultipart(
            @ModelAttribute CoverPhotoDto dto,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        CoverPhotoDto updated = coverPhotoService.updateCoverPhoto(dto, file);
        return ResponseEntity.ok(updated);
    }
}
