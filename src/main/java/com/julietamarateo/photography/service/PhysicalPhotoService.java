package com.julietamarateo.photography.service;

import com.julietamarateo.photography.dto.CustomAttributeDto;
import com.julietamarateo.photography.dto.PhysicalPhotoDto;
import com.julietamarateo.photography.dto.PreferenceResponseDto;
import com.julietamarateo.photography.entity.PhysicalPhoto;
import com.julietamarateo.photography.exception.ResourceNotFoundException;
import com.julietamarateo.photography.repository.PhysicalPhotoRepository;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PhysicalPhotoService {

    private static final Logger log = LoggerFactory.getLogger(PhysicalPhotoService.class);

    private final PhysicalPhotoRepository photoRepository;
    private final FileStorageService fileStorageService;

    @Value("${mercadopago.back-urls.success:http://localhost:4200/?status=approved}")
    private String backUrlSuccess;

    @Value("${mercadopago.back-urls.failure:http://localhost:4200/?status=rejected}")
    private String backUrlFailure;

    @Value("${mercadopago.back-urls.pending:http://localhost:4200/?status=pending}")
    private String backUrlPending;

    public PhysicalPhotoService(PhysicalPhotoRepository photoRepository, FileStorageService fileStorageService) {
        this.photoRepository = photoRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public List<PhysicalPhotoDto> getPublicPhotos() {
        List<PhysicalPhoto> photos = photoRepository.findByIsActiveTrueOrderByDisplayOrderAscCreatedAtDesc();
        if (photos.isEmpty() && photoRepository.count() == 0) {
            seedDefaultPhotos();
            photos = photoRepository.findByIsActiveTrueOrderByDisplayOrderAscCreatedAtDesc();
        }
        return photos.stream().map(PhysicalPhotoDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public List<PhysicalPhotoDto> getAllPhotosForAdmin() {
        List<PhysicalPhoto> photos = photoRepository.findAllByOrderByDisplayOrderAscCreatedAtDesc();
        if (photos.isEmpty() && photoRepository.count() == 0) {
            seedDefaultPhotos();
            photos = photoRepository.findAllByOrderByDisplayOrderAscCreatedAtDesc();
        }
        return photos.stream().map(PhysicalPhotoDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PhysicalPhotoDto getPhotoById(String id) {
        PhysicalPhoto entity = photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Foto física no encontrada con ID: " + id));
        return PhysicalPhotoDto.fromEntity(entity);
    }

    @Transactional
    public PhysicalPhotoDto createPhoto(PhysicalPhotoDto dto, MultipartFile file) {
        String id = dto.getId();
        if (id == null || id.isBlank()) {
            id = "phys-" + UUID.randomUUID().toString().substring(0, 12);
            dto.setId(id);
        }

        if (file != null && !file.isEmpty()) {
            String uploadedUrl = fileStorageService.storeFile(file, "physical-photos");
            dto.setImageUrl(uploadedUrl);
        }

        if (dto.getImageUrl() == null || dto.getImageUrl().isBlank()) {
            throw new IllegalArgumentException("La foto física debe tener una imagen asignada");
        }

        PhysicalPhoto entity = dto.toEntity();
        entity.setCreatedAt(LocalDateTime.now());
        PhysicalPhoto saved = photoRepository.save(entity);
        log.info("Foto física creada con ID: {}", saved.getId());
        return PhysicalPhotoDto.fromEntity(saved);
    }

    @Transactional
    public PhysicalPhotoDto updatePhoto(String id, PhysicalPhotoDto dto, MultipartFile file) {
        PhysicalPhoto existing = photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Foto física no encontrada con ID: " + id));

        if (file != null && !file.isEmpty()) {
            String uploadedUrl = fileStorageService.storeFile(file, "physical-photos");
            existing.setImageUrl(uploadedUrl);
        } else if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
            existing.setImageUrl(dto.getImageUrl());
        }

        if (dto.getTitle() != null) existing.setTitle(dto.getTitle());
        if (dto.getPrice() != null) existing.setPrice(dto.getPrice());
        if (dto.getCurrency() != null) existing.setCurrency(dto.getCurrency());
        if (dto.getIsActive() != null) existing.setIsActive(dto.getIsActive());
        if (dto.getIsSoldOut() != null) existing.setIsSoldOut(dto.getIsSoldOut());
        if (dto.getMercadoPagoUrl() != null) existing.setMercadoPagoUrl(dto.getMercadoPagoUrl());
        if (dto.getDisplayOrder() != null) existing.setDisplayOrder(dto.getDisplayOrder());

        if (dto.getCustomAttributes() != null) {
            PhysicalPhoto temp = dto.toEntity();
            existing.setCustomAttributesJson(temp.getCustomAttributesJson());
        }

        PhysicalPhoto saved = photoRepository.save(existing);
        log.info("Foto física actualizada con ID: {}", saved.getId());
        return PhysicalPhotoDto.fromEntity(saved);
    }

    @Transactional
    public void deletePhoto(String id) {
        if (!photoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Foto física no encontrada con ID: " + id);
        }
        photoRepository.deleteById(id);
        log.info("Foto física eliminada con ID: {}", id);
    }

    @Transactional
    public void reorderPhotos(List<String> orderedIds) {
        if (orderedIds == null || orderedIds.isEmpty()) return;
        for (int i = 0; i < orderedIds.size(); i++) {
            final int order = i;
            String photoId = orderedIds.get(i);
            photoRepository.findById(photoId).ifPresent(p -> {
                p.setDisplayOrder(order);
                photoRepository.save(p);
            });
        }
        log.info("Reordenamiento de {} fotos físicas completado", orderedIds.size());
    }

    /**
     * Genera una preferencia dinámica de Mercado Pago Checkout Pro para una foto física.
     */
    @Transactional(readOnly = true)
    public PreferenceResponseDto createPreferenceForPhoto(String id, int quantity) {
        PhysicalPhoto photo = photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Foto física no encontrada con ID: " + id));

        if (Boolean.TRUE.equals(photo.getIsSoldOut())) {
            throw new IllegalStateException("Esta pieza física se encuentra agotada");
        }

        int finalQty = Math.max(1, Math.min(quantity, 2)); // 1 o 2 unidades máximo

        // Si la foto tiene un link directo de Mercado Pago cargado manualmente por el admin, retornarlo
        if (photo.getMercadoPagoUrl() != null && !photo.getMercadoPagoUrl().isBlank()) {
            return new PreferenceResponseDto(
                    "manual-" + photo.getId(),
                    photo.getMercadoPagoUrl().trim(),
                    photo.getMercadoPagoUrl().trim()
            );
        }

        // Generar Checkout Pro dinámico con SDK de Mercado Pago
        try {
            List<PreferenceItemRequest> items = new ArrayList<>();
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(photo.getId())
                    .title("Copia Física: " + photo.getTitle())
                    .description("Impresión fotográfica Fine Art de autor firmada")
                    .quantity(finalQty)
                    .unitPrice(BigDecimal.valueOf(photo.getPrice()))
                    .currencyId(photo.getCurrency() != null ? photo.getCurrency() : "ARS")
                    .build();
            items.add(itemRequest);

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(backUrlSuccess != null ? backUrlSuccess.trim() : "http://localhost:4200/?status=approved")
                    .failure(backUrlFailure != null ? backUrlFailure.trim() : "http://localhost:4200/?status=rejected")
                    .pending(backUrlPending != null ? backUrlPending.trim() : "http://localhost:4200/?status=pending")
                    .build();

            PreferenceRequest request = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    .externalReference("PHYSICAL-" + photo.getId() + "-" + System.currentTimeMillis())
                    .build();

            PreferenceClient client = new PreferenceClient();
            var preference = client.create(request);

            log.info("Preferencia Checkout Pro generada para foto física {}: ID={}, initPoint={}",
                    photo.getId(), preference.getId(), preference.getInitPoint());

            return new PreferenceResponseDto(
                    preference.getId(),
                    preference.getInitPoint(),
                    preference.getSandboxInitPoint()
            );
        } catch (Exception e) {
            log.error("Error al generar preferencia dinámica de Mercado Pago para foto {}: {}", photo.getId(), e.getMessage());
            throw new RuntimeException("Error al conectar con Mercado Pago: " + e.getMessage(), e);
        }
    }

    private void seedDefaultPhotos() {
        log.info("Sembrando fotos físicas de muestra en la base de datos...");

        PhysicalPhoto p1 = new PhysicalPhoto();
        p1.setId("phys-001");
        p1.setTitle("Luces de Shibuya en Medianoche");
        p1.setImageUrl("https://images.unsplash.com/photo-1503899036084-c55cdd92da26?auto=format&fit=crop&w=1200&q=85");
        p1.setPrice(45000.0);
        p1.setCurrency("ARS");
        p1.setIsActive(true);
        p1.setIsSoldOut(false);
        p1.setDisplayOrder(0);
        p1.setCreatedAt(LocalDateTime.now());
        List<CustomAttributeDto> a1 = List.of(
                new CustomAttributeDto("Tamaño", "40x60 cm"),
                new CustomAttributeDto("Papel", "Hahnemühle Photo Rag 308g"),
                new CustomAttributeDto("Enmarcado", "Madera Kiri natural con vidrio antirreflejo"),
                new CustomAttributeDto("Edición", "Serie limitada de 15 copias firmadas")
        );
        PhysicalPhotoDto d1 = new PhysicalPhotoDto();
        d1.setCustomAttributes(a1);
        p1.setCustomAttributesJson(d1.toEntity().getCustomAttributesJson());
        photoRepository.save(p1);

        PhysicalPhoto p2 = new PhysicalPhoto();
        p2.setId("phys-002");
        p2.setTitle("Silencio en los Fiordos");
        p2.setImageUrl("https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1200&q=85");
        p2.setPrice(38000.0);
        p2.setCurrency("ARS");
        p2.setIsActive(true);
        p2.setIsSoldOut(false);
        p2.setDisplayOrder(1);
        p2.setCreatedAt(LocalDateTime.now().minusDays(1));
        List<CustomAttributeDto> a2 = List.of(
                new CustomAttributeDto("Tamaño", "30x45 cm"),
                new CustomAttributeDto("Papel", "Fine Art Baryta 325g Brillo Satinado"),
                new CustomAttributeDto("Enmarcado", "Varilla negra mate minimalista"),
                new CustomAttributeDto("Tirada", "Firmada y numerada al dorso")
        );
        PhysicalPhotoDto d2 = new PhysicalPhotoDto();
        d2.setCustomAttributes(a2);
        p2.setCustomAttributesJson(d2.toEntity().getCustomAttributesJson());
        photoRepository.save(p2);

        PhysicalPhoto p3 = new PhysicalPhoto();
        p3.setId("phys-003");
        p3.setTitle("Atardecer Dorado en la Quebrada");
        p3.setImageUrl("https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?auto=format&fit=crop&w=1200&q=85");
        p3.setPrice(52000.0);
        p3.setCurrency("ARS");
        p3.setIsActive(true);
        p3.setIsSoldOut(false);
        p3.setDisplayOrder(2);
        p3.setCreatedAt(LocalDateTime.now().minusDays(2));
        List<CustomAttributeDto> a3 = List.of(
                new CustomAttributeDto("Tamaño", "50x75 cm"),
                new CustomAttributeDto("Papel", "Canson Rag Photographique 310g"),
                new CustomAttributeDto("Enmarcado", "Marco box de madera paraíso con paspartú"),
                new CustomAttributeDto("Certificado", "Incluye certificado de autenticidad hológrafo")
        );
        PhysicalPhotoDto d3 = new PhysicalPhotoDto();
        d3.setCustomAttributes(a3);
        p3.setCustomAttributesJson(d3.toEntity().getCustomAttributesJson());
        photoRepository.save(p3);

        log.info("3 fotos físicas de muestra creadas exitosamente.");
    }
}
