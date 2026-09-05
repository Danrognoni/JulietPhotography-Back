package com.julietamarateo.photography.controller;

import com.julietamarateo.photography.dto.AlbumLayoutDto;
import com.julietamarateo.photography.dto.PhotoLayoutDto;
import com.julietamarateo.photography.service.AlbumService;
import com.julietamarateo.photography.service.PhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
