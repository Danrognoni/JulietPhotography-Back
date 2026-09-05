package com.julietamarateo.photography.service;

import com.julietamarateo.photography.dto.AlbumDto;
import com.julietamarateo.photography.dto.AlbumLayoutDto;
import com.julietamarateo.photography.dto.AlbumPhotoDto;
import com.julietamarateo.photography.dto.ReorderPhotosDto;
import com.julietamarateo.photography.entity.Album;
import com.julietamarateo.photography.entity.AlbumPhoto;
import com.julietamarateo.photography.entity.Photo;
import com.julietamarateo.photography.exception.ResourceNotFoundException;
import com.julietamarateo.photography.repository.AlbumPhotoRepository;
import com.julietamarateo.photography.repository.AlbumRepository;
import com.julietamarateo.photography.repository.PhotoRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumPhotoRepository albumPhotoRepository;
    private final PhotoRepository photoRepository;
    private final FileStorageService fileStorageService;

    public AlbumService(AlbumRepository albumRepository,
                        AlbumPhotoRepository albumPhotoRepository,
                        PhotoRepository photoRepository,
                        FileStorageService fileStorageService) {
        this.albumRepository = albumRepository;
        this.albumPhotoRepository = albumPhotoRepository;
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
                .or(() -> albumRepository.findByNameIgnoreCase(id))
                .orElseThrow(() -> new ResourceNotFoundException("Álbum no encontrado con ID o Slug: " + id));
        return enrichAlbumDto(album);
    }

    @Transactional
    @CacheEvict(value = {"albums", "photos"}, allEntries = true)
    public AlbumDto createAlbum(AlbumDto dto, MultipartFile file) {
        String albumName = dto.getTitle() != null && !dto.getTitle().isBlank()
                ? dto.getTitle().trim()
                : (dto.getName() != null ? dto.getName().trim() : "");

        if (albumName.isBlank()) {
            throw new IllegalArgumentException("El título del álbum es obligatorio");
        }

        Album album = dto.toEntity();
        album.setName(albumName);

        if (album.getId() == null || album.getId().isBlank()) {
            String slug = toSlug(albumName);
            if (slug.isBlank() || albumRepository.existsById(slug)) {
                slug = slug + "-" + UUID.randomUUID().toString().substring(0, 6);
            }
            album.setId(slug);
        }

        if (file != null && !file.isEmpty()) {
            String uploadedUrl = fileStorageService.storeFile(file, "albums");
            album.setCoverImage(uploadedUrl);
        } else if (dto.getCoverImageUrl() != null && !dto.getCoverImageUrl().isBlank()) {
            album.setCoverImage(dto.getCoverImageUrl().trim());
        } else if (dto.getCoverImage() != null && !dto.getCoverImage().isBlank()) {
            album.setCoverImage(dto.getCoverImage().trim());
        }

        Album saved = albumRepository.save(album);
        return enrichAlbumDto(saved);
    }

    @Transactional
    @CacheEvict(value = {"albums", "photos"}, allEntries = true)
    public AlbumDto updateAlbum(String id, AlbumDto dto, MultipartFile file) {
        Album existing = albumRepository.findById(id)
                .or(() -> albumRepository.findByNameIgnoreCase(id))
                .orElseThrow(() -> new ResourceNotFoundException("Álbum no encontrado con ID: " + id));

        String oldName = existing.getName();

        dto.applyToEntity(existing);

        if (file != null && !file.isEmpty()) {
            fileStorageService.deleteFile(existing.getCoverImage());
            String newUploadedUrl = fileStorageService.storeFile(file, "albums");
            existing.setCoverImage(newUploadedUrl);
        } else if (dto.getCoverImageUrl() != null && !dto.getCoverImageUrl().isBlank()) {
            existing.setCoverImage(dto.getCoverImageUrl().trim());
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
                .or(() -> albumRepository.findByNameIgnoreCase(id))
                .orElseThrow(() -> new ResourceNotFoundException("Álbum no encontrado con ID: " + id));

        fileStorageService.deleteFile(existing.getCoverImage());

        // Eliminar archivos físicos de las fotos del álbum si existen
        if (existing.getPhotos() != null) {
            for (AlbumPhoto p : existing.getPhotos()) {
                fileStorageService.deleteFile(p.getImageUrl());
            }
        }

        albumRepository.delete(existing);
    }

    @Transactional
    @CacheEvict(value = {"albums", "photos"}, allEntries = true)
    public AlbumPhotoDto addPhotoToAlbum(String albumId, AlbumPhotoDto dto) {
        Album album = albumRepository.findById(albumId)
                .or(() -> albumRepository.findByNameIgnoreCase(albumId))
                .orElseThrow(() -> new ResourceNotFoundException("Álbum no encontrado con ID: " + albumId));

        if (dto.getImageUrl() == null || dto.getImageUrl().isBlank()) {
            throw new IllegalArgumentException("La URL de la imagen es obligatoria");
        }

        String photoId = dto.getId() != null && !dto.getId().isBlank() ? dto.getId() : UUID.randomUUID().toString();
        int nextOrder = album.getPhotos() != null ? album.getPhotos().size() + 1 : 1;
        Integer order = dto.getDisplayOrder() != null && dto.getDisplayOrder() > 0 ? dto.getDisplayOrder() : nextOrder;

        AlbumPhoto photo = new AlbumPhoto(
                photoId,
                album,
                dto.getImageUrl().trim(),
                dto.getCaption(),
                dto.getOrientation() != null ? dto.getOrientation() : "portrait",
                order
        );

        if (dto.getX() != null) photo.setX(dto.getX());
        if (dto.getY() != null) photo.setY(dto.getY());
        if (dto.getWidth() != null) photo.setWidth(dto.getWidth());
        if (dto.getHeight() != null) photo.setHeight(dto.getHeight());
        if (dto.getRotation() != null) photo.setRotation(dto.getRotation());
        if (dto.getZIndex() != null) photo.setZIndex(dto.getZIndex());

        AlbumPhoto saved = albumPhotoRepository.save(photo);
        album.getPhotos().add(saved);
        albumRepository.save(album);

        return AlbumPhotoDto.fromEntity(saved);
    }

    @Transactional
    @CacheEvict(value = {"albums", "photos"}, allEntries = true)
    public List<AlbumPhotoDto> addPhotosMultipartToAlbum(String albumId, List<MultipartFile> files, String caption, String orientation) {
        Album album = albumRepository.findById(albumId)
                .or(() -> albumRepository.findByNameIgnoreCase(albumId))
                .orElseThrow(() -> new ResourceNotFoundException("Álbum no encontrado con ID: " + albumId));

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("Se requiere al menos un archivo de imagen");
        }

        List<AlbumPhotoDto> createdPhotos = new ArrayList<>();
        int currentCount = album.getPhotos() != null ? album.getPhotos().size() : 0;

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (file.isEmpty()) continue;

            String uploadedUrl = fileStorageService.storeFile(file, "albums/" + album.getId());
            String photoId = UUID.randomUUID().toString();
            AlbumPhoto photo = new AlbumPhoto(
                    photoId,
                    album,
                    uploadedUrl,
                    caption,
                    orientation != null ? orientation : "portrait",
                    currentCount + i + 1
            );
            AlbumPhoto saved = albumPhotoRepository.save(photo);
            album.getPhotos().add(saved);
            createdPhotos.add(AlbumPhotoDto.fromEntity(saved));
        }

        albumRepository.save(album);
        return createdPhotos;
    }

    @Transactional
    @CacheEvict(value = {"albums", "photos"}, allEntries = true)
    public void deletePhoto(String photoId) {
        AlbumPhoto albumPhoto = albumPhotoRepository.findById(photoId).orElse(null);
        if (albumPhoto != null) {
            fileStorageService.deleteFile(albumPhoto.getImageUrl());
            Album album = albumPhoto.getAlbum();
            if (album != null && album.getPhotos() != null) {
                album.getPhotos().remove(albumPhoto);
            }
            albumPhotoRepository.delete(albumPhoto);
            return;
        }

        // Si no está en album_photos, verificar en la tabla general de fotos
        Photo generalPhoto = photoRepository.findById(photoId).orElse(null);
        if (generalPhoto != null) {
            fileStorageService.deleteFile(generalPhoto.getImageUrl());
            photoRepository.delete(generalPhoto);
            return;
        }

        throw new ResourceNotFoundException("Foto no encontrada con ID: " + photoId);
    }

    @Transactional
    @CacheEvict(value = {"albums", "photos"}, allEntries = true)
    public void reorderPhotos(String albumId, ReorderPhotosDto dto) {
        if (dto == null || dto.getItems() == null) return;

        for (ReorderPhotosDto.PhotoOrderItem item : dto.getItems()) {
            if (item.getId() != null) {
                albumPhotoRepository.findById(item.getId()).ifPresent(photo -> {
                    photo.setDisplayOrder(item.getOrder() != null ? item.getOrder() : 0);
                    albumPhotoRepository.save(photo);
                });
            }
        }
    }

    @Transactional
    @CacheEvict(value = {"albums"}, allEntries = true)
    public void updateAlbumsLayout(List<AlbumLayoutDto> layouts) {
        if (layouts == null || layouts.isEmpty()) return;

        for (AlbumLayoutDto item : layouts) {
            if (item.getId() == null) continue;

            Album album = albumRepository.findById(item.getId())
                    .or(() -> albumRepository.findByNameIgnoreCase(item.getId()))
                    .orElse(null);

            if (album != null) {
                if (item.getXPos() != null) album.setXPos(item.getXPos());
                if (item.getYPos() != null) album.setYPos(item.getYPos());
                if (item.getWidth() != null) album.setWidth(item.getWidth());
                if (item.getHeight() != null) album.setHeight(item.getHeight());
                if (item.getRotation() != null) album.setRotation(item.getRotation());
                if (item.getZIndex() != null) album.setZIndex(item.getZIndex());
                album.setUpdatedAt(LocalDateTime.now());
                albumRepository.save(album);
            }
        }
    }

    private AlbumDto enrichAlbumDto(Album entity) {
        AlbumDto dto = AlbumDto.fromEntity(entity);
        List<AlbumPhoto> associatedPhotos = albumPhotoRepository.findByAlbumIdOrderByDisplayOrderAscCreatedAtAsc(entity.getId());
        if (associatedPhotos != null && !associatedPhotos.isEmpty()) {
            dto.setPhotos(associatedPhotos.stream().map(AlbumPhotoDto::fromEntity).collect(Collectors.toList()));
            dto.setCount(associatedPhotos.size());
        } else {
            // Conteo fallback con fotos de categoría si no hay fotos directas de álbum
            int photoCount = photoRepository.findByCategoryOrderByCreatedAtDesc(entity.getName()).size();
            dto.setCount(photoCount);
        }
        return dto;
    }

    public String toSlug(String input) {
        if (input == null) return "";
        Pattern nonLatin = Pattern.compile("[^\\w-]");
        Pattern whitespace = Pattern.compile("[\\s]");
        String nowhitespace = whitespace.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = nonLatin.matcher(normalized).replaceAll("").toLowerCase(Locale.ENGLISH);
        return slug.replaceAll("-+", "-").replaceAll("^-|-$", "");
    }
}
