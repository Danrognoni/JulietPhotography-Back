package com.julietamarateo.photography.controller;

import com.julietamarateo.photography.dto.ServiceItemDto;
import com.julietamarateo.photography.service.ServiceItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceItemService serviceItemService;

    public ServiceController(ServiceItemService serviceItemService) {
        this.serviceItemService = serviceItemService;
    }

    /**
     * Endpoint público para consultar los servicios ofrecidos.
     */
    @GetMapping
    public ResponseEntity<List<ServiceItemDto>> getAllServices() {
        List<ServiceItemDto> services = serviceItemService.getAllServices();
        return ResponseEntity.ok(services);
    }

    /**
     * Endpoint público para consultar un servicio por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServiceItemDto> getServiceById(@PathVariable String id) {
        ServiceItemDto service = serviceItemService.getServiceById(id);
        return ResponseEntity.ok(service);
    }

    /**
     * Endpoint protegido para crear un servicio (JSON).
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceItemDto> createServiceJson(@Valid @RequestBody ServiceItemDto dto) {
        ServiceItemDto created = serviceItemService.createService(dto, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Endpoint protegido para crear un servicio con archivo físico (Multipart).
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceItemDto> createServiceMultipart(
            @ModelAttribute ServiceItemDto dto,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        ServiceItemDto created = serviceItemService.createService(dto, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Endpoint protegido para actualizar un servicio (JSON).
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceItemDto> updateServiceJson(
            @PathVariable String id,
            @RequestBody ServiceItemDto dto) {
        ServiceItemDto updated = serviceItemService.updateService(id, dto, null);
        return ResponseEntity.ok(updated);
    }

    /**
     * Endpoint protegido para actualizar un servicio con archivo físico (Multipart).
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceItemDto> updateServiceMultipart(
            @PathVariable String id,
            @ModelAttribute ServiceItemDto dto,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        ServiceItemDto updated = serviceItemService.updateService(id, dto, file);
        return ResponseEntity.ok(updated);
    }

    /**
     * Endpoint protegido para eliminar un servicio.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteService(@PathVariable String id) {
        serviceItemService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
