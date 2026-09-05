package com.julietamarateo.photography.service;

import com.julietamarateo.photography.dto.AlbumDto;
import com.julietamarateo.photography.entity.Album;
import com.julietamarateo.photography.entity.Photo;
import com.julietamarateo.photography.exception.ResourceNotFoundException;
import com.julietamarateo.photography.repository.AlbumRepository;
import com.julietamarateo.photography.repository.PhotoRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final PhotoRepository photoRepository;
    private final FileStorageService fileStorageService;

    public AlbumService(AlbumRepository albumRepository,
                        PhotoRepository photoRepository,
                        FileStorageService fileStorageService) {
        this.albumRepository = albumRepository;
        this.photoRepository = photoRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "albums", key = "'all'")
    public List<AlbumDto> getAllAlbums() {
        return albumRepository.findAllByOrderByDisplayOrderAscCreatedAtAsc().stream()
                .map(this::enrichAlbumDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "albums", key = "#id")
    public AlbumDto getAlbumById(String id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum no encontrado con ID: " + id));
        return enrichAlbumDto(album);
    }

    @Transactional
    @CacheEvict(value = {"albums", "photos"}, allEntries = true)
    public AlbumDto createAlbum(AlbumDto dto, MultipartFile file) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre del álbum es obligatorio");
        }

        Album album = dto.toEntity();

        if (album.getId() == null || album.getId().isBlank()) {
            String slug = toSlug(album.getName());
            if (slug.isBlank() || albumRepository.existsById(slug)) {
                slug = slug + "-" + UUID.randomUUID().toString().substring(0, 6);
            }
            album.setId(slug);
        }

        if (file != null && !file.isEmpty()) {
            String uploadedUrl = fileStorageService.storeFile(file, "albums");
            album.setCoverImage(uploadedUrl);
        } else if (dto.getCoverImage() != null) {
            album.setCoverImage(dto.getCoverImage().trim());
        }

        Album saved = albumRepository.save(album);
        return enrichAlbumDto(saved);
    }

    @Transactional
    @CacheEvict(value = {"albums", "photos"}, allEntries = true)
    public AlbumDto updateAlbum(String id, AlbumDto dto, MultipartFile file) {
        Album existing = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum no encontrado con ID: " + id));

        String oldName = existing.getName();

        dto.applyToEntity(existing);

        if (file != null && !file.isEmpty()) {
            fileStorageService.deleteFile(existing.getCoverImage());
            String newUploadedUrl = fileStorageService.storeFile(file, "albums");
            existing.setCoverImage(newUploadedUrl);
        } else if (dto.getCoverImage() != null && !dto.getCoverImage().isBlank()) {
            existing.setCoverImage(dto.getCoverImage().trim());
        }

        // Si cambió el nombre del álbum, sincronizar las fotos que pertenecían a la categoría anterior
        if (dto.getName() != null && !dto.getName().isBlank() && !oldName.equalsIgnoreCase(existing.getName())) {
            List<Photo> matchingPhotos = photoRepository.findByCategoryOrderByCreatedAtDesc(oldName);
            for (Photo p : matchingPhotos) {
                p.setCategory(existing.getName());
            }
            photoRepository.saveAll(matchingPhotos);
        }

        existing.setUpdatedAt(LocalDateTime.now());
        Album saved = albumRepository.save(existing);
        return enrichAlbumDto(saved);
    }

    @Transactional
    @CacheEvict(value = {"albums", "photos"}, allEntries = true)
    public void deleteAlbum(String id) {
        Album existing = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum no encontrado con ID: " + id));

        fileStorageService.deleteFile(existing.getCoverImage());
        albumRepository.delete(existing);
    }

    private AlbumDto enrichAlbumDto(Album entity) {
        AlbumDto dto = AlbumDto.fromEntity(entity);
        // Calcular conteo dinámico basado en fotos con esa categoría
        int photoCount = photoRepository.findByCategoryOrderByCreatedAtDesc(entity.getName()).size();
        if (entity.getPhotoUrls() != null && entity.getPhotoUrls().size() > photoCount) {
            photoCount = entity.getPhotoUrls().size();
        }
        dto.setCount(photoCount);
        return dto;
    }

    private String toSlug(String input) {
        if (input == null) return "";
        Pattern nonLatin = Pattern.compile("[^\\w-]");
        Pattern whitespace = Pattern.compile("[\\s]");
        String nowhitespace = whitespace.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = nonLatin.matcher(normalized).replaceAll("").toLowerCase(Locale.ENGLISH);
        return slug.replaceAll("-+", "-").replaceAll("^-|-$", "");
    }
}
