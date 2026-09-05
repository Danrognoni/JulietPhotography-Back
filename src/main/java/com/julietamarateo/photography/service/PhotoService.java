package com.julietamarateo.photography.service;

import com.julietamarateo.photography.dto.PageResponse;
import com.julietamarateo.photography.dto.PhotoDto;
import com.julietamarateo.photography.entity.Photo;
import com.julietamarateo.photography.exception.ResourceNotFoundException;
import com.julietamarateo.photography.entity.AlbumPhoto;
import com.julietamarateo.photography.repository.AlbumPhotoRepository;
import com.julietamarateo.photography.repository.PhotoRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final AlbumPhotoRepository albumPhotoRepository;
    private final FileStorageService fileStorageService;

    public PhotoService(PhotoRepository photoRepository,
                        AlbumPhotoRepository albumPhotoRepository,
                        FileStorageService fileStorageService) {
        this.photoRepository = photoRepository;
        this.albumPhotoRepository = albumPhotoRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "photos", key = "'all_' + (#category != null ? #category : '') + '_' + (#query != null ? #query : '')")
    public List<PhotoDto> getAllPhotos(String category, String query) {
        List<Photo> photos;
        String cleanCat = (category != null && !category.isBlank() && !category.equalsIgnoreCase("Todos"))
                ? category.trim()
                : null;
        String cleanQuery = (query != null && !query.isBlank()) ? query.trim() : null;

        if (cleanCat == null && cleanQuery == null) {
            photos = photoRepository.findAllByOrderByCreatedAtDesc();
        } else {
            photos = photoRepository.searchPhotos(cleanCat, cleanQuery);
        }

        return photos.stream()
                .map(PhotoDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "photos", key = "'paged_' + (#category != null ? #category : '') + '_' + (#query != null ? #query : '') + '_' + #page + '_' + #size")
    public PageResponse<PhotoDto> getPhotosPaged(String category, String query, int page, int size) {
        String cleanCat = (category != null && !category.isBlank() && !category.equalsIgnoreCase("Todos"))
                ? category.trim()
                : null;
        String cleanQuery = (query != null && !query.isBlank()) ? query.trim() : null;

        int pageIndex = Math.max(0, page);
        int pageSize = size > 0 ? size : 12;
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Photo> photoPage = photoRepository.searchPhotosPaged(cleanCat, cleanQuery, pageable);

        List<PhotoDto> dtos = photoPage.getContent().stream()
                .map(PhotoDto::fromEntity)
                .collect(Collectors.toList());

        return new PageResponse<>(
                dtos,
                photoPage.getNumber(),
                photoPage.getSize(),
                photoPage.getTotalElements(),
                photoPage.getTotalPages(),
                photoPage.isFirst(),
                photoPage.isLast()
        );
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "photos", key = "'id_' + #id")
    public PhotoDto getPhotoById(String id) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fotografía no encontrada con ID: " + id));
        return PhotoDto.fromEntity(photo);
    }

    @Transactional
    @CacheEvict(value = "photos", allEntries = true)
    public PhotoDto createPhoto(PhotoDto dto, MultipartFile file) {
        Photo photo = dto.toEntity();

        if (photo.getId() == null || photo.getId().isBlank()) {
            photo.setId("photo-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4));
        }

        // Si se subió un archivo físico, almacenarlo y actualizar la URL pública y thumbnail
        if (file != null && !file.isEmpty()) {
            String uploadedUrl = fileStorageService.storeFile(file, "photos");
            photo.setImageUrl(uploadedUrl);
            photo.setThumbnailUrl(fileStorageService.getThumbnailUrl(uploadedUrl));
        } else if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
            photo.setImageUrl(dto.getImageUrl().trim());
            if (dto.getThumbnailUrl() != null && !dto.getThumbnailUrl().isBlank()) {
                photo.setThumbnailUrl(dto.getThumbnailUrl().trim());
            } else {
                photo.setThumbnailUrl(dto.getImageUrl().trim());
            }
        } else {
            throw new IllegalArgumentException("Debe proporcionar una imagen (archivo o URL)");
        }

        Photo saved = photoRepository.save(photo);
        return PhotoDto.fromEntity(saved);
    }

    @Transactional
    @CacheEvict(value = "photos", allEntries = true)
    public PhotoDto updatePhoto(String id, PhotoDto dto, MultipartFile file) {
        Photo existing = photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fotografía no encontrada con ID: " + id));

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            existing.setTitle(dto.getTitle().trim());
        }
        if (dto.getCategory() != null && !dto.getCategory().isBlank()) {
            existing.setCategory(dto.getCategory().trim());
        }
        if (dto.getPrice() != null) {
            existing.setPrice(dto.getPrice());
        }
        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription().trim());
        }
        if (dto.getDimensions() != null) {
            existing.setDimensions(dto.getDimensions().trim());
        }
        if (dto.getTechnicalSheet() != null) {
            existing.setTechnicalSheet(dto.getTechnicalSheet().trim());
        }
        if (dto.getInStock() != null) {
            existing.setInStock(dto.getInStock());
        }
        if (dto.getBadge() != null) {
            existing.setBadge(dto.getBadge().trim());
        }
        if (dto.getFeatured() != null) {
            existing.setFeatured(dto.getFeatured());
        }

        if (dto.getCameraDetails() != null) {
            if (dto.getCameraDetails().getCamera() != null) existing.setCamera(dto.getCameraDetails().getCamera().trim());
            if (dto.getCameraDetails().getLens() != null) existing.setLens(dto.getCameraDetails().getLens().trim());
            if (dto.getCameraDetails().getAperture() != null) existing.setAperture(dto.getCameraDetails().getAperture().trim());
            if (dto.getCameraDetails().getShutter() != null) existing.setShutter(dto.getCameraDetails().getShutter().trim());
            if (dto.getCameraDetails().getIso() != null) existing.setIso(dto.getCameraDetails().getIso().trim());
        }

        // Si se envía un nuevo archivo físico
        if (file != null && !file.isEmpty()) {
            // Eliminar imagen anterior si era un upload local
            fileStorageService.deleteFile(existing.getImageUrl());
            String newUploadedUrl = fileStorageService.storeFile(file, "photos");
            existing.setImageUrl(newUploadedUrl);
            existing.setThumbnailUrl(fileStorageService.getThumbnailUrl(newUploadedUrl));
        } else if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
            existing.setImageUrl(dto.getImageUrl().trim());
            if (dto.getThumbnailUrl() != null && !dto.getThumbnailUrl().isBlank()) {
                existing.setThumbnailUrl(dto.getThumbnailUrl().trim());
            }
        }

        Photo saved = photoRepository.save(existing);
        return PhotoDto.fromEntity(saved);
    }

    @Transactional
    @CacheEvict(value = {"photos", "albums"}, allEntries = true)
    public void deletePhoto(String id) {
        Photo photo = photoRepository.findById(id).orElse(null);
        if (photo != null) {
            fileStorageService.deleteFile(photo.getImageUrl());
            photoRepository.delete(photo);
            return;
        }

        AlbumPhoto albumPhoto = albumPhotoRepository.findById(id).orElse(null);
        if (albumPhoto != null) {
            fileStorageService.deleteFile(albumPhoto.getImageUrl());
            if (albumPhoto.getAlbum() != null && albumPhoto.getAlbum().getPhotos() != null) {
                albumPhoto.getAlbum().getPhotos().remove(albumPhoto);
            }
            albumPhotoRepository.delete(albumPhoto);
            return;
        }

        throw new ResourceNotFoundException("Fotografía no encontrada con ID: " + id);
    }
}

