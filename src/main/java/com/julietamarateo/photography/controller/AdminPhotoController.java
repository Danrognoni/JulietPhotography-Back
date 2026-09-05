package com.julietamarateo.photography.controller;

import com.julietamarateo.photography.dto.PhotoLayoutDto;
import com.julietamarateo.photography.service.PhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/photos")
public class AdminPhotoController {

    private final PhotoService photoService;

    public AdminPhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    /**
     * Endpoint protegido para actualizar las coordenadas y dimensiones de un lote de fotos en el canvas.
     * PUT /api/admin/photos/layout -> [{ id, x, y, width, height, zIndex }]
     */
    @PutMapping("/layout")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updatePhotosLayout(@RequestBody List<PhotoLayoutDto> layouts) {
        photoService.updatePhotosLayout(layouts);
        return ResponseEntity.ok().build();
    }
}
