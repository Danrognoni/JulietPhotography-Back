package com.julietamarateo.photography.service;

import com.julietamarateo.photography.dto.ServiceItemDto;
import com.julietamarateo.photography.entity.ServiceItem;
import com.julietamarateo.photography.exception.ResourceNotFoundException;
import com.julietamarateo.photography.repository.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ServiceItemService {

    private final ServiceRepository serviceRepository;
    private final FileStorageService fileStorageService;

    public ServiceItemService(ServiceRepository serviceRepository, FileStorageService fileStorageService) {
        this.serviceRepository = serviceRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public List<ServiceItemDto> getAllServices() {
        return serviceRepository.findAllByOrderByCreatedAtAsc().stream()
                .map(ServiceItemDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ServiceItemDto getServiceById(String id) {
        ServiceItem item = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con ID: " + id));
        return ServiceItemDto.fromEntity(item);
    }

    @Transactional
    public ServiceItemDto createService(ServiceItemDto dto, MultipartFile file) {
        ServiceItem item = dto.toEntity();

        if (item.getId() == null || item.getId().isBlank()) {
            item.setId("serv-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4));
        }

        if (file != null && !file.isEmpty()) {
            String uploadedUrl = fileStorageService.storeFile(file, "services");
            item.setImageUrl(uploadedUrl);
        }

        ServiceItem saved = serviceRepository.save(item);
        return ServiceItemDto.fromEntity(saved);
    }

    @Transactional
    public ServiceItemDto updateService(String id, ServiceItemDto dto, MultipartFile file) {
        ServiceItem existing = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con ID: " + id));

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            existing.setTitle(dto.getTitle().trim());
        }
        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription().trim());
        }
        if (dto.getFeatures() != null) {
            existing.setFeatures(dto.getFeatures());
        }
        if (dto.getWhatsappUrl() != null) {
            existing.setWhatsappUrl(dto.getWhatsappUrl().trim());
        }
        if (dto.getPrice() != null) {
            existing.setPrice(dto.getPrice());
        }

        if (file != null && !file.isEmpty()) {
            fileStorageService.deleteFile(existing.getImageUrl());
            String newUploadedUrl = fileStorageService.storeFile(file, "services");
            existing.setImageUrl(newUploadedUrl);
        } else if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
            existing.setImageUrl(dto.getImageUrl().trim());
        }

        ServiceItem saved = serviceRepository.save(existing);
        return ServiceItemDto.fromEntity(saved);
    }

    @Transactional
    public void deleteService(String id) {
        ServiceItem existing = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con ID: " + id));

        fileStorageService.deleteFile(existing.getImageUrl());
        serviceRepository.delete(existing);
    }
}
