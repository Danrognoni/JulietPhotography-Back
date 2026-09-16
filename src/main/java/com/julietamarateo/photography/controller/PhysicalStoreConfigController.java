package com.julietamarateo.photography.controller;

import com.julietamarateo.photography.dto.PhysicalStoreConfigDto;
import com.julietamarateo.photography.service.PhysicalStoreService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping({"/api/physical-store", "/physical-store"})
public class PhysicalStoreConfigController {

    private final PhysicalStoreService storeService;

    public PhysicalStoreConfigController(PhysicalStoreService storeService) {
        this.storeService = storeService;
    }

    /**
     * Consulta pública de la configuración de la tienda / sección física.
     */
    @GetMapping("/config")
    public ResponseEntity<PhysicalStoreConfigDto> getConfig() {
        return ResponseEntity.ok(storeService.getConfig());
    }

    /**
     * Actualización protegida para la administradora.
     */
    @PutMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhysicalStoreConfigDto> updateConfig(@RequestBody PhysicalStoreConfigDto dto) {
        return ResponseEntity.ok(storeService.updateConfig(dto));
    }

    /**
     * Subida de imagen de fondo / banner para la sección.
     */
    @PostMapping(value = "/upload-bg", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> uploadBackgroundImage(@RequestParam("file") MultipartFile file) {
        String url = storeService.uploadBackgroundImage(file);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
