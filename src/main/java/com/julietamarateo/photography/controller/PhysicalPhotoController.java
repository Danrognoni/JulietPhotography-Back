package com.julietamarateo.photography.controller;

import com.julietamarateo.photography.dto.PhysicalPhotoDto;
import com.julietamarateo.photography.dto.PreferenceResponseDto;
import com.julietamarateo.photography.service.PhysicalPhotoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping({"/api/physical-photos", "/physical-photos"})
public class PhysicalPhotoController {

    private final PhysicalPhotoService physicalPhotoService;

    public PhysicalPhotoController(PhysicalPhotoService physicalPhotoService) {
        this.physicalPhotoService = physicalPhotoService;
    }

    /**
     * Endpoint público para listar fotos físicas activas en venta.
     */
    @GetMapping
    public ResponseEntity<List<PhysicalPhotoDto>> getPublicPhotos() {
        List<PhysicalPhotoDto> photos = physicalPhotoService.getPublicPhotos();
        return ResponseEntity.ok(photos);
    }

    /**
     * Endpoint protegido para que la administradora vea todas las fotos (activas, ocultas y agotadas).
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PhysicalPhotoDto>> getAllPhotosForAdmin() {
        List<PhysicalPhotoDto> photos = physicalPhotoService.getAllPhotosForAdmin();
        return ResponseEntity.ok(photos);
    }

    /**
     * Endpoint público para obtener el detalle de una foto física.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PhysicalPhotoDto> getPhotoById(@PathVariable String id) {
        PhysicalPhotoDto photo = physicalPhotoService.getPhotoById(id);
        return ResponseEntity.ok(photo);
    }

    /**
     * Endpoint público para generar la preferencia de Checkout Pro de Mercado Pago para compra directa sin carrito.
     */
    @PostMapping("/{id}/preference")
    public ResponseEntity<PreferenceResponseDto> createPreference(
            @PathVariable String id,
            @RequestParam(defaultValue = "1") int quantity) {
        PreferenceResponseDto preference = physicalPhotoService.createPreferenceForPhoto(id, quantity);
        return ResponseEntity.ok(preference);
    }

    /**
     * Endpoint protegido para crear una foto física con subida de archivo multipart.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhysicalPhotoDto> createPhotoMultipart(
            @ModelAttribute PhysicalPhotoDto dto,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        PhysicalPhotoDto created = physicalPhotoService.createPhoto(dto, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Endpoint protegido alternativo para crear una foto física enviando JSON directo con URL.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhysicalPhotoDto> createPhotoJson(@Valid @RequestBody PhysicalPhotoDto dto) {
        PhysicalPhotoDto created = physicalPhotoService.createPhoto(dto, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Endpoint protegido para actualizar una foto física con nuevo archivo multipart opcional.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhysicalPhotoDto> updatePhotoMultipart(
            @PathVariable String id,
            @ModelAttribute PhysicalPhotoDto dto,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        PhysicalPhotoDto updated = physicalPhotoService.updatePhoto(id, dto, file);
        return ResponseEntity.ok(updated);
    }

    /**
     * Endpoint protegido para actualizar una foto física enviando JSON.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhysicalPhotoDto> updatePhotoJson(
            @PathVariable String id,
            @RequestBody PhysicalPhotoDto dto) {
        PhysicalPhotoDto updated = physicalPhotoService.updatePhoto(id, dto, null);
        return ResponseEntity.ok(updated);
    }

    /**
     * Endpoint protegido para eliminar una foto física.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePhoto(@PathVariable String id) {
        physicalPhotoService.deletePhoto(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint protegido para reordenar fotos físicas.
     */
    @PutMapping("/reorder")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> reorderPhotos(@RequestBody List<String> orderedIds) {
        physicalPhotoService.reorderPhotos(orderedIds);
        return ResponseEntity.ok().build();
    }
}
