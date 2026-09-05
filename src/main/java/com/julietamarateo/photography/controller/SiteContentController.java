package com.julietamarateo.photography.controller;

import com.julietamarateo.photography.dto.SiteContentDto;
import com.julietamarateo.photography.service.SiteContentService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/site-content")
public class SiteContentController {

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
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "field", required = false) String field) {
        String url = siteContentService.uploadSiteImage(file, field);
        return ResponseEntity.ok(Map.of("url", url, "field", field != null ? field : ""));
    }
}
