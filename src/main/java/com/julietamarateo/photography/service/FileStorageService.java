package com.julietamarateo.photography.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private final Cloudinary cloudinary;

    public FileStorageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Sube un archivo a Cloudinary y retorna su URL pública segura (secure_url).
     *
     * @param file      Archivo multipart subido.
     * @param subfolder Carpeta de destino en Cloudinary (e.g. photos, profile, services, albums, site).
     * @return URL pública segura HTTPS del recurso en Cloudinary.
     */
    public String storeFile(MultipartFile file, String subfolder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No se puede almacenar un archivo vacío");
        }

        Map<String, Object> options = new HashMap<>();
        if (subfolder != null && !subfolder.isBlank()) {
            options.put("folder", subfolder);
        }
        options.put("resource_type", "auto");

        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (IOException e) {
            log.error("Error al leer los bytes del archivo para subida a Cloudinary", e);
            throw new RuntimeException("Error al leer el archivo: " + e.getMessage(), e);
        }

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    fileBytes,
                    options.isEmpty() ? ObjectUtils.emptyMap() : options
            );

            String secureUrl = (String) uploadResult.get("secure_url");
            if (secureUrl == null || secureUrl.isBlank()) {
                secureUrl = (String) uploadResult.get("url");
            }

            log.info("Archivo subido a Cloudinary exitosamente: {}", secureUrl);
            return secureUrl;
        } catch (Exception e) {
            log.error("Error al subir archivo a Cloudinary: {}", e.getMessage(), e);
            throw new RuntimeException("Error al subir archivo a Cloudinary: " + e.getMessage(), e);
        }
    }

    /**
     * Sobrecarga para subir archivos a la raíz de Cloudinary sin subcarpeta específica.
     */
    public String storeFile(MultipartFile file) {
        return storeFile(file, null);
    }

    /**
     * Retorna la URL del thumbnail correspondiente para una URL de imagen.
     * Si es una URL de Cloudinary, genera la URL con transformación optimizada al vuelo.
     */
    public String getThumbnailUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return fileUrl;
        }

        // Si es una URL de Cloudinary, inyectar transformaciones al vuelo para miniatura ultrarrápida
        if (fileUrl.contains("cloudinary.com") && fileUrl.contains("/upload/")) {
            if (fileUrl.contains("/upload/c_scale,w_480,q_auto,f_auto/")) {
                return fileUrl;
            }
            return fileUrl.replace("/upload/", "/upload/c_scale,w_480,q_auto,f_auto/");
        }

        // Compatibilidad hacia atrás para archivos locales previos
        if (fileUrl.startsWith("/uploads/")) {
            if (fileUrl.contains("_thumb.")) {
                return fileUrl;
            }
            int dotIndex = fileUrl.lastIndexOf('.');
            if (dotIndex > 0) {
                return fileUrl.substring(0, dotIndex) + "_thumb.jpg";
            }
            return fileUrl + "_thumb.jpg";
        }

        return fileUrl;
    }

    /**
     * Elimina el archivo de forma segura a partir de su URL pública (Cloudinary o almacenamiento local).
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return false;
        }

        if (fileUrl.contains("cloudinary.com")) {
            try {
                String publicId = extractPublicId(fileUrl);
                if (publicId != null && !publicId.isBlank()) {
                    Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                    log.info("Archivo eliminado de Cloudinary: publicId={}, resultado={}", publicId, result.get("result"));
                    return true;
                }
            } catch (Exception e) {
                log.error("No se pudo eliminar el archivo de Cloudinary: {} - {}", fileUrl, e.getMessage());
                return false;
            }
        }

        if (fileUrl.startsWith("/uploads/") || fileUrl.startsWith("uploads/")) {
            try {
                String relPath = fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl;
                Path filePath = Paths.get(relPath).toAbsolutePath().normalize();
                boolean deleted = Files.deleteIfExists(filePath);

                // Limpiar también thumbnail local si existe
                String thumbUrl = getThumbnailUrl(fileUrl);
                if (thumbUrl != null && !thumbUrl.equals(fileUrl)) {
                    String thumbRel = thumbUrl.startsWith("/") ? thumbUrl.substring(1) : thumbUrl;
                    Files.deleteIfExists(Paths.get(thumbRel).toAbsolutePath().normalize());
                }
                log.info("Archivo local eliminado: {}, resultado={}", filePath, deleted);
                return deleted;
            } catch (Exception e) {
                log.error("No se pudo eliminar el archivo local: {} - {}", fileUrl, e.getMessage());
                return false;
            }
        }

        return false;
    }

    /**
     * Extrae el public_id de Cloudinary a partir de una URL completa.
     * Ejemplo: https://res.cloudinary.com/demo/image/upload/v1612345678/photos/sample.jpg -> photos/sample
     */
    public String extractPublicId(String fileUrl) {
        if (fileUrl == null) return null;
        int uploadIdx = fileUrl.indexOf("/upload/");
        if (uploadIdx == -1) return null;

        String path = fileUrl.substring(uploadIdx + "/upload/".length());

        // Si incluye parámetros de transformación (ej. c_scale,w_480/ o v1234567/)
        // Ignorar segmentos de transformación si los hay antes del versionado
        while (path.startsWith("c_") || path.startsWith("w_") || path.startsWith("h_") || path.startsWith("q_")) {
            int slash = path.indexOf('/');
            if (slash != -1) {
                path = path.substring(slash + 1);
            } else {
                break;
            }
        }

        // Si tiene prefijo de versión 'v1234567890/', removerlo
        if (path.matches("^v[0-9]+/.*")) {
            path = path.substring(path.indexOf('/') + 1);
        }

        // Remover extensión de archivo si existe (.jpg, .png, .webp, etc.)
        int dotIdx = path.lastIndexOf('.');
        if (dotIdx > 0) {
            path = path.substring(0, dotIdx);
        }

        return path;
    }
}
