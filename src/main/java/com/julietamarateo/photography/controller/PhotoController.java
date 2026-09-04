package com.julietamarateo.photography.controller;

import com.julietamarateo.photography.dto.PhotoDto;
import com.julietamarateo.photography.service.PhotoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    /**
     * Endpoint público para listar y filtrar fotos.
     */
    @GetMapping
    public ResponseEntity<List<PhotoDto>> getAllPhotos(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q) {
        List<PhotoDto> photos = photoService.getAllPhotos(category, q);
        return ResponseEntity.ok(photos);
    }

    /**
     * Endpoint público para obtener una foto por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PhotoDto> getPhotoById(@PathVariable String id) {
        PhotoDto photo = photoService.getPhotoById(id);
        return ResponseEntity.ok(photo);
    }

    /**
     * Endpoint protegido para crear una foto enviando archivo físico (Multipart).
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhotoDto> createPhotoMultipart(
            @ModelAttribute PhotoDto dto,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        PhotoDto created = photoService.createPhoto(dto, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Endpoint protegido alternativo para crear una foto enviando JSON directo con URL externa.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhotoDto> createPhotoJson(@Valid @RequestBody PhotoDto dto) {
        PhotoDto created = photoService.createPhoto(dto, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Endpoint protegido para actualizar una foto con posible nuevo archivo físico.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhotoDto> updatePhotoMultipart(
            @PathVariable String id,
            @ModelAttribute PhotoDto dto,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        PhotoDto updated = photoService.updatePhoto(id, dto, file);
        return ResponseEntity.ok(updated);
    }

    /**
     * Endpoint protegido para actualizar una foto enviando JSON.
     */
    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.ALL_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhotoDto> updatePhotoJson(
            @PathVariable String id,
            @RequestBody PhotoDto dto) {
        PhotoDto updated = photoService.updatePhoto(id, dto, null);
        return ResponseEntity.ok(updated);
    }

    /**
     * Endpoint protegido para eliminar una foto.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePhoto(@PathVariable String id) {
        photoService.deletePhoto(id);
        return ResponseEntity.noContent().build();
    }
}
