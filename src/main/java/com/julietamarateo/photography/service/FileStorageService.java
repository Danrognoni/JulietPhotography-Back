package com.julietamarateo.photography.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

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
     * Guarda físicamente un archivo en el servidor y retorna su URL relativa pública.
     */
    public String storeFile(MultipartFile file, String subfolder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No se puede almacenar un archivo vacío");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "image.jpg");
        // Sanitizar el nombre del archivo
        originalFilename = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex).toLowerCase();
        }

        String uniqueFileName = UUID.randomUUID().toString().substring(0, 12) + "_" + originalFilename;

        try {
            Path targetDir = this.rootLocation.resolve(subfolder != null ? subfolder : "");
            Files.createDirectories(targetDir);

            Path targetLocation = targetDir.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Retornar ruta pública accesible vía HTTP
            String publicPath = "/uploads/" + (subfolder != null && !subfolder.isEmpty() ? subfolder + "/" : "") + uniqueFileName;
            return publicPath;
        } catch (IOException ex) {
            throw new RuntimeException("Error al guardar el archivo físico " + uniqueFileName, ex);
        }
    }

    /**
     * Elimina un archivo físico del servidor de forma segura.
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

            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("No se pudo eliminar el archivo: " + fileUrl + " - " + e.getMessage());
            return false;
        }
    }
}
