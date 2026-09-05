package com.julietamarateo.photography.controller;

import com.julietamarateo.photography.dto.AlbumDto;
import com.julietamarateo.photography.dto.AlbumPhotoDto;
import com.julietamarateo.photography.dto.ReorderPhotosDto;
import com.julietamarateo.photography.service.AlbumService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    /**
     * Endpoint público para consultar la lista de todos los álbumes con portada y orden.
     */
    @GetMapping
    public ResponseEntity<List<AlbumDto>> getAllAlbums() {
        List<AlbumDto> albums = albumService.getAllAlbums();
        return ResponseEntity.ok(albums);
    }

    /**
     * Endpoint público para consultar un álbum individual por su ID o slug, incluyendo fotos ordenadas.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AlbumDto> getAlbumById(@PathVariable String id) {
        AlbumDto album = albumService.getAlbumById(id);
        return ResponseEntity.ok(album);
    }

    /**
     * Endpoint protegido para crear un álbum enviando JSON directo.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlbumDto> createAlbumJson(@Valid @RequestBody AlbumDto dto) {
        AlbumDto created = albumService.createAlbum(dto, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Endpoint protegido para crear un álbum enviando archivo de portada física (Multipart).
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlbumDto> createAlbumMultipart(
            @ModelAttribute AlbumDto dto,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        AlbumDto created = albumService.createAlbum(dto, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Endpoint protegido para actualizar un álbum mediante JSON.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlbumDto> updateAlbumJson(
            @PathVariable String id,
            @RequestBody AlbumDto dto) {
        AlbumDto updated = albumService.updateAlbum(id, dto, null);
        return ResponseEntity.ok(updated);
    }

    /**
     * Endpoint protegido para actualizar un álbum con posible nuevo archivo de portada física (Multipart).
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlbumDto> updateAlbumMultipart(
            @PathVariable String id,
            @ModelAttribute AlbumDto dto,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        AlbumDto updated = albumService.updateAlbum(id, dto, file);
        return ResponseEntity.ok(updated);
    }

    /**
     * Endpoint protegido para eliminar un álbum y sus fotos asociadas en cascada.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAlbum(@PathVariable String id) {
        albumService.deleteAlbum(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint protegido para asociar una nueva foto al álbum mediante JSON (con URL).
     */
    @PostMapping(value = "/{id}/photos", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlbumPhotoDto> addPhotoJson(
            @PathVariable String id,
            @RequestBody AlbumPhotoDto dto) {
        AlbumPhotoDto created = albumService.addPhotoToAlbum(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Endpoint protegido para subir archivo(s) de foto física al álbum (Multipart).
     */
    @PostMapping(value = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AlbumPhotoDto>> addPhotosMultipart(
            @PathVariable String id,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "caption", required = false) String caption,
            @RequestParam(value = "orientation", required = false, defaultValue = "portrait") String orientation) {
        List<AlbumPhotoDto> created = albumService.addPhotosMultipartToAlbum(id, files, caption, orientation);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Endpoint protegido para eliminar una foto específica de un álbum.
     */
    @DeleteMapping("/{id}/photos/{photoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAlbumPhoto(
            @PathVariable String id,
            @PathVariable String photoId) {
        albumService.deletePhoto(photoId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint protegido para reordenar fotos dentro de un álbum.
     */
    @PutMapping("/{id}/photos/reorder")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> reorderAlbumPhotos(
            @PathVariable String id,
            @RequestBody ReorderPhotosDto dto) {
        albumService.reorderPhotos(id, dto);
        return ResponseEntity.ok().build();
    }
}
