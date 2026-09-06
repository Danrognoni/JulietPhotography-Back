package com.julietamarateo.photography.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private final Cloudinary cloudinary;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public FileStorageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Verifica si las credenciales de Cloudinary están debidamente provistas sin lanzar NullPointerException.
     */
    public boolean isCloudinaryConfigured() {
        if (cloudinary == null || cloudinary.config == null) {
            return false;
        }
        String name = cloudinary.config.cloudName;
        String key = cloudinary.config.apiKey;
        String secret = cloudinary.config.apiSecret;
        return name != null && !name.trim().isEmpty()
                && key != null && !key.trim().isEmpty()
                && secret != null && !secret.trim().isEmpty();
    }

    /**
     * Sube un archivo a Cloudinary (o fallback en disco local) y retorna su URL pública segura.
     *
     * @param file      Archivo multipart subido.
     * @param subfolder Carpeta de destino (e.g. photos, profile, services, albums, site).
     * @return URL pública segura HTTPS del recurso.
     */
    public String storeFile(MultipartFile file, String subfolder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No se puede almacenar un archivo vacío");
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            log.error("Error al extraer bytes del archivo multipart: {}", e.getMessage(), e);
            throw new IllegalArgumentException("No se pudo leer el archivo cargado: " + e.getMessage(), e);
        }

        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("El archivo cargado no contiene datos válidos");
        }

        // Si Cloudinary no está configurado (ej. entorno local sin credenciales), usar almacenamiento en disco local
        if (!isCloudinaryConfigured()) {
            log.warn("Cloudinary no está configurado (faltan credenciales). Almacenando archivo en disco local...");
            return storeFileLocally(file, bytes, subfolder);
        }

        Map<String, Object> options = new HashMap<>();
        if (subfolder != null && !subfolder.isBlank()) {
            options.put("folder", subfolder);
        }
        options.put("resource_type", "auto");
        // Optimizaciones automáticas Cloudinary
        options.put("quality", "auto");
        options.put("fetch_format", "auto");

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    bytes,
                    options.isEmpty() ? ObjectUtils.emptyMap() : options
            );

            String secureUrl = (String) uploadResult.get("secure_url");
            if (secureUrl == null || secureUrl.isBlank()) {
                secureUrl = (String) uploadResult.get("url");
            }

            log.info("Archivo subido a Cloudinary exitosamente: {}", secureUrl);
            return secureUrl;
        } catch (Exception e) {
            log.error("Error al procesar o subir archivo a Cloudinary: {}", e.getMessage(), e);
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("quota") || msg.contains("limit")) {
                throw new RuntimeException("Límite de cuota en Cloudinary alcanzado. Por favor, libere espacio o contacte al administrador.", e);
            }
            if (msg.contains("file size") || msg.contains("too large")) {
                throw new RuntimeException("El archivo excede el tamaño o dimensiones permitidas por el servidor multimedia.", e);
            }
            if (msg.contains("must supply") || msg.contains("cloud_name") || msg.contains("api_key") || msg.contains("api_secret") || msg.contains("credentials")) {
                throw new RuntimeException("Credenciales de Cloudinary incompletas o inválidas: " + e.getMessage(), e);
            }
            throw new RuntimeException("Error al subir archivo a Cloudinary: " + e.getMessage(), e);
        }
    }

    /**
     * Almacena el archivo en el sistema de archivos local cuando Cloudinary no está disponible.
     */
    public String storeFileLocally(MultipartFile file, byte[] bytes, String subfolder) {
        try {
            String folder = uploadDir != null && !uploadDir.isBlank() ? uploadDir : "uploads";
            if (subfolder != null && !subfolder.isBlank()) {
                folder = folder + "/" + subfolder;
            }
            Path targetDir = Paths.get(folder).toAbsolutePath().normalize();
            Files.createDirectories(targetDir);

            String orig = file.getOriginalFilename();
            String ext = ".jpg";
            if (orig != null && orig.contains(".")) {
                ext = orig.substring(orig.lastIndexOf('.'));
            }
            String fileName = UUID.randomUUID().toString() + ext;
            Path dest = targetDir.resolve(fileName);
            Files.write(dest, bytes);

            String relativeUrl = "/" + folder.replace('\\', '/') + "/" + fileName;
            log.info("Archivo almacenado exitosamente en disco local: {}", relativeUrl);
            return relativeUrl;
        } catch (IOException ex) {
            log.error("Error al persistir archivo en almacenamiento local: {}", ex.getMessage(), ex);
            throw new RuntimeException("Error al almacenar archivo en disco: " + ex.getMessage(), ex);
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
     * Retorna una URL con transformación de ancho máximo optimizada al vuelo para Cloudinary.
     */
    public String getOptimizedUrl(String fileUrl, int maxWidth) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return fileUrl;
        }
        if (fileUrl.contains("cloudinary.com") && fileUrl.contains("/upload/")) {
            String transform = "c_limit,w_" + maxWidth + ",q_auto,f_auto";
            if (fileUrl.contains("/upload/" + transform + "/")) {
                return fileUrl;
            }
            return fileUrl.replace("/upload/", "/upload/" + transform + "/");
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
