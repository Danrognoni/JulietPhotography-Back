package com.julietamarateo.photography.controller;

import com.julietamarateo.photography.dto.SiteContentDto;
import com.julietamarateo.photography.service.SiteContentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({"/api/site-content", "/site-content"})
public class SiteContentController {

    private static final Logger log = LoggerFactory.getLogger(SiteContentController.class);

    private final SiteContentService siteContentService;

    public SiteContentController(SiteContentService siteContentService) {
        this.siteContentService = siteContentService;
    }

    @GetMapping
    public ResponseEntity<SiteContentDto> getSiteContent() {
        return ResponseEntity.ok(siteContentService.getSiteContent());
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SiteContentDto> updateSiteContent(@RequestBody SiteContentDto dto) {
        return ResponseEntity.ok(siteContentService.updateSiteContent(dto));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "field", required = false) String field) {
        if (file == null || file.isEmpty()) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("timestamp", LocalDateTime.now());
            errorBody.put("status", HttpStatus.BAD_REQUEST.value());
            errorBody.put("error", "Archivo Inválido");
            errorBody.put("message", "No se ha seleccionado ningún archivo o el archivo está vacío");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
        }

        try {
            String url = siteContentService.uploadSiteImage(file, field);
            Map<String, String> response = new HashMap<>();
            response.put("url", url != null ? url : "");
            response.put("field", field != null ? field : "");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            log.warn("Validación fallida en upload site-content: {}", ex.getMessage());
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("timestamp", LocalDateTime.now());
            errorBody.put("status", HttpStatus.BAD_REQUEST.value());
            errorBody.put("error", "Error de Validación");
            errorBody.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
        } catch (Exception ex) {
            log.error("Fallo al subir imagen en site-content: {}", ex.getMessage(), ex);
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("timestamp", LocalDateTime.now());
            errorBody.put("status", HttpStatus.BAD_GATEWAY.value());
            errorBody.put("error", "Error de Almacenamiento Multimedia");
            errorBody.put("message", ex.getMessage() != null ? ex.getMessage() : "Error desconocido al procesar el archivo en el servidor");
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorBody);
        }
    }
}
