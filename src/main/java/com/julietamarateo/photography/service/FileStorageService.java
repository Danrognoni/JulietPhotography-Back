package com.julietamarateo.photography.service;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private static final int MAX_MAIN_WIDTH = 2048;
    private static final int MAX_MAIN_HEIGHT = 2048;
    private static final float MAIN_JPEG_QUALITY = 0.85f;

    private static final int MAX_THUMB_WIDTH = 480;
    private static final int MAX_THUMB_HEIGHT = 480;
    private static final float THUMB_JPEG_QUALITY = 0.80f;

    private final Path rootLocation;

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
            Files.createDirectories(this.rootLocation.resolve("photos"));
            Files.createDirectories(this.rootLocation.resolve("services"));
            Files.createDirectories(this.rootLocation.resolve("profile"));
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar la carpeta de almacenamiento de archivos", e);
        }
    }

    /**
     * Almacena y procesa automáticamente la imagen subida:
     * - Comprime y optimiza la imagen principal (resolución máx 2048px, calidad 85%).
     * - Genera automáticamente una miniatura (thumbnail) de alto rendimiento (máx 480px, calidad 80%).
     * - Retorna la URL pública de la imagen principal.
     */
    public String storeFile(MultipartFile file, String subfolder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No se puede almacenar un archivo vacío");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "image.jpg");
        originalFilename = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");

        String nameWithoutExt = originalFilename;
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            nameWithoutExt = originalFilename.substring(0, dotIndex);
        }

        String prefix = UUID.randomUUID().toString().substring(0, 12);
        String uniqueFileName = prefix + "_" + nameWithoutExt + ".jpg";
        String thumbFileName = prefix + "_" + nameWithoutExt + "_thumb.jpg";

        try {
            Path targetDir = this.rootLocation.resolve(subfolder != null ? subfolder : "");
            Files.createDirectories(targetDir);

            Path targetLocation = targetDir.resolve(uniqueFileName);
            Path thumbLocation = targetDir.resolve(thumbFileName);

            boolean processed = false;
            try (InputStream in = file.getInputStream()) {
                BufferedImage originalImage = ImageIO.read(in);
                if (originalImage != null) {
                    BufferedImage convertedImage = prepareOpaqueImage(originalImage);

                    // 1. Versión optimizada principal (Web / Full HD+)
                    Thumbnails.of(convertedImage)
                            .size(MAX_MAIN_WIDTH, MAX_MAIN_HEIGHT)
                            .outputFormat("jpg")
                            .outputQuality(MAIN_JPEG_QUALITY)
                            .toFile(targetLocation.toFile());

                    // 2. Miniatura de carga ultrarrápida (Thumbnail)
                    Thumbnails.of(convertedImage)
                            .size(MAX_THUMB_WIDTH, MAX_THUMB_HEIGHT)
                            .outputFormat("jpg")
                            .outputQuality(THUMB_JPEG_QUALITY)
                            .toFile(thumbLocation.toFile());

                    processed = true;
                    log.info("Imagen procesada y comprimida: {} (principal y miniatura generadas)", uniqueFileName);
                }
            } catch (Exception e) {
                log.warn("No se pudo procesar la imagen con Thumbnailator ({}), recurriendo a guardado directo", e.getMessage());
            }

            if (!processed) {
                // Fallback para archivos no decodificables como imagen o bytes de prueba en tests
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                Files.copy(file.getInputStream(), thumbLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            // Retornar ruta pública accesible vía HTTP
            return "/uploads/" + (subfolder != null && !subfolder.isEmpty() ? subfolder + "/" : "") + uniqueFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Error al guardar el archivo físico " + uniqueFileName, ex);
        }
    }

    /**
     * Retorna la URL del thumbnail correspondiente para una URL de imagen guardada.
     */
    public String getThumbnailUrl(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith("/uploads/")) {
            return fileUrl;
        }
        if (fileUrl.contains("_thumb.")) {
            return fileUrl;
        }
        int dotIndex = fileUrl.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileUrl.substring(0, dotIndex) + "_thumb.jpg";
        }
        return fileUrl + "_thumb.jpg";
    }

    /**
     * Elimina el archivo físico del servidor y su respectivo thumbnail de forma segura.
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith("/uploads/")) {
            return false;
        }

        try {
            String relative = fileUrl.substring("/uploads/".length());
            Path filePath = this.rootLocation.resolve(relative).normalize();

            // Evitar Path Traversal
            if (!filePath.startsWith(this.rootLocation)) {
                return false;
            }

            boolean deleted = Files.deleteIfExists(filePath);

            // Eliminar miniatura asociada si existe
            String thumbUrl = getThumbnailUrl(fileUrl);
            if (thumbUrl != null && !thumbUrl.equals(fileUrl)) {
                Path thumbPath = this.rootLocation.resolve(thumbUrl.substring("/uploads/".length())).normalize();
                if (thumbPath.startsWith(this.rootLocation)) {
                    Files.deleteIfExists(thumbPath);
                }
            }

            return deleted;
        } catch (IOException e) {
            log.error("No se pudo eliminar el archivo físico: {} - {}", fileUrl, e.getMessage());
            return false;
        }
    }

    private BufferedImage prepareOpaqueImage(BufferedImage source) {
        if (source.getTransparency() == BufferedImage.OPAQUE) {
            return source;
        }
        BufferedImage opaque = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = opaque.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, source.getWidth(), source.getHeight());
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();
        return opaque;
    }
}
