package com.julietamarateo.photography.controller;

import com.julietamarateo.photography.dto.AlbumLayoutDto;
import com.julietamarateo.photography.dto.PhotoDto;
import com.julietamarateo.photography.dto.PhotoLayoutDto;
import com.julietamarateo.photography.service.AlbumService;
import com.julietamarateo.photography.service.PhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping({"/api/admin", "/admin"})
public class AdminPhotoController {

    private final PhotoService photoService;
    private final AlbumService albumService;

    public AdminPhotoController(PhotoService photoService, AlbumService albumService) {
        this.photoService = photoService;
        this.albumService = albumService;
    }

    /**
     * Endpoint protegido para crear una foto enviando archivo físico desde la ruta de administración.
     * POST /api/admin/photos
     */
    @PostMapping(value = "/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhotoDto> createPhotoMultipart(
            @ModelAttribute PhotoDto dto,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        PhotoDto created = photoService.createPhoto(dto, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Endpoint protegido para actualizar una foto con archivo físico desde la ruta de administración.
     * PUT /api/admin/photos/{id}
     */
    @PutMapping(value = "/photos/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhotoDto> updatePhotoMultipart(
            @PathVariable String id,
            @ModelAttribute PhotoDto dto,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        PhotoDto updated = photoService.updatePhoto(id, dto, file);
        return ResponseEntity.ok(updated);
    }

    /**
     * Endpoint protegido para actualizar las coordenadas y dimensiones de un lote de fotos en el canvas.
     * PUT /api/admin/photos/layout -> [{ id, x, y, width, height, zIndex }]
     */
    @PutMapping("/photos/layout")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updatePhotosLayout(@RequestBody List<PhotoLayoutDto> layouts) {
        photoService.updatePhotosLayout(layouts);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint protegido para actualizar las coordenadas y dimensiones de un lote de álbumes en el canvas.
     * PUT /api/admin/albums/layout -> [{ id, xPos, yPos, width, zIndex }]
     */
    @PutMapping("/albums/layout")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateAlbumsLayout(@RequestBody List<AlbumLayoutDto> layouts) {
        albumService.updateAlbumsLayout(layouts);
        return ResponseEntity.ok().build();
    }
}
