package com.julietamarateo.photography;

import com.cloudinary.Cloudinary;
import com.cloudinary.ProgressCallback;
import com.cloudinary.Uploader;
import com.cloudinary.strategies.AbstractUploaderStrategy;
import com.julietamarateo.photography.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FileStorageServiceTest {

    private boolean failUpload = false;
    private Map<String, Object> lastUploadOptions;
    private String lastDestroyPublicId;

    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        failUpload = false;
        lastUploadOptions = null;
        lastDestroyPublicId = null;

        AbstractUploaderStrategy strategy = new AbstractUploaderStrategy() {
            @Override
            public Map callApi(String action, Map<String, Object> params, Map options, Object file, ProgressCallback progressCallback) throws IOException {
                if ("destroy".equalsIgnoreCase(action)) {
                    lastDestroyPublicId = (params != null && params.containsKey("public_id")) ? (String) params.get("public_id") : null;
                    return Map.of("result", "ok");
                }

                if (failUpload) {
                    throw new IOException("Simulated network failure");
                }

                lastUploadOptions = options != null ? new HashMap<>(options) : new HashMap<>();
                String folder = (options != null && options.containsKey("folder")) ? (String) options.get("folder") : "photos";
                return Map.of(
                        "secure_url", "https://res.cloudinary.com/demo/image/upload/v1612345678/" + folder + "/test.jpg",
                        "public_id", folder + "/test"
                );
            }
        };

        Cloudinary fakeCloudinary = new Cloudinary(Map.of("cloud_name", "test", "api_key", "test", "api_secret", "test")) {
            @Override
            public Uploader uploader() {
                return new Uploader(this, strategy);
            }
        };

        fileStorageService = new FileStorageService(fakeCloudinary);
    }

    @Test
    @DisplayName("storeFile con subcarpeta debe subir archivo con options y retornar secure_url")
    void testStoreFileWithSubfolder() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "sample-bytes".getBytes()
        );

        String resultUrl = fileStorageService.storeFile(file, "photos");

        assertNotNull(resultUrl);
        assertTrue(resultUrl.contains("res.cloudinary.com"));
        assertTrue(resultUrl.contains("/photos/test.jpg"));
        assertNotNull(lastUploadOptions);
        assertEquals("photos", lastUploadOptions.get("folder"));
        assertEquals("auto", lastUploadOptions.get("resource_type"));
    }

    @Test
    @DisplayName("storeFile con archivo vacío debe lanzar IllegalArgumentException")
    void testStoreFileEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);
        assertThrows(IllegalArgumentException.class, () -> fileStorageService.storeFile(emptyFile, "photos"));
    }

    @Test
    @DisplayName("storeFile cuando uploader lanza IOException debe propagar RuntimeException")
    void testStoreFileIOException() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "sample-bytes".getBytes()
        );

        failUpload = true;

        RuntimeException ex = assertThrows(RuntimeException.class, () -> fileStorageService.storeFile(file, "photos"));
        assertTrue(ex.getMessage().contains("Error al subir archivo a Cloudinary"));
    }

    @Test
    @DisplayName("getThumbnailUrl para URL de Cloudinary debe inyectar transformaciones c_scale,w_480,q_auto,f_auto")
    void testGetThumbnailUrlCloudinary() {
        String originalUrl = "https://res.cloudinary.com/demo/image/upload/v1612345678/photos/sample.jpg";
        String thumbUrl = fileStorageService.getThumbnailUrl(originalUrl);

        assertTrue(thumbUrl.contains("/upload/c_scale,w_480,q_auto,f_auto/"));
        assertTrue(thumbUrl.endsWith("v1612345678/photos/sample.jpg"));
    }

    @Test
    @DisplayName("getThumbnailUrl para URLs externas no Cloudinary debe retornarse sin cambios")
    void testGetThumbnailUrlExternal() {
        String unsplash = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format";
        assertEquals(unsplash, fileStorageService.getThumbnailUrl(unsplash));
    }

    @Test
    @DisplayName("extractPublicId debe extraer correctamente el id sin versión ni extensión")
    void testExtractPublicId() {
        String urlWithVersion = "https://res.cloudinary.com/demo/image/upload/v1612345678/photos/sample.jpg";
        assertEquals("photos/sample", fileStorageService.extractPublicId(urlWithVersion));

        String urlWithoutVersion = "https://res.cloudinary.com/demo/image/upload/albums/landscape.png";
        assertEquals("albums/landscape", fileStorageService.extractPublicId(urlWithoutVersion));
    }

    @Test
    @DisplayName("deleteFile de Cloudinary debe llamar a uploader.destroy con el public_id extraído")
    void testDeleteFileCloudinary() {
        String cloudinaryUrl = "https://res.cloudinary.com/demo/image/upload/v1612345678/photos/sample.jpg";

        boolean deleted = fileStorageService.deleteFile(cloudinaryUrl);
        assertTrue(deleted);
        assertEquals("photos/sample", lastDestroyPublicId);
    }
}
