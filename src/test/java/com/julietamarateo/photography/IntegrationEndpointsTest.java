package com.julietamarateo.photography;

import com.julietamarateo.photography.config.JwtTokenProvider;
import com.julietamarateo.photography.entity.Photo;
import com.julietamarateo.photography.repository.PhotoRepository;
import com.julietamarateo.photography.repository.ProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import com.julietamarateo.photography.service.FileStorageService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(IntegrationEndpointsTest.MockStorageConfig.class)
public class IntegrationEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private ProfileRepository profileRepository;

    private String adminToken;

    @TestConfiguration
    static class MockStorageConfig {
        @Bean
        @Primary
        public FileStorageService testFileStorageService() {
            return new FileStorageService(null) {
                @Override
                public String storeFile(org.springframework.web.multipart.MultipartFile file, String subfolder) {
                    String folder = (subfolder != null && !subfolder.isBlank()) ? subfolder : "photos";
                    return "https://res.cloudinary.com/julietphotography/image/upload/v1700000000/" + folder + "/test-photo.jpg";
                }

                @Override
                public String storeFile(org.springframework.web.multipart.MultipartFile file) {
                    return storeFile(file, "photos");
                }

                @Override
                public String getThumbnailUrl(String fileUrl) {
                    if (fileUrl == null) return null;
                    return fileUrl.replace("/upload/", "/upload/c_scale,w_480,q_auto,f_auto/");
                }

                @Override
                public boolean deleteFile(String fileUrl) {
                    return true;
                }
            };
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        adminToken = jwtTokenProvider.generateToken("julietamarateo4@gmail.com", "ROLE_ADMIN");

        profileRepository.findTopByOrderByIdAsc().ifPresent(prof -> {
            prof.setName("Julieta Marateo");
            profileRepository.save(prof);
        });

        if (!photoRepository.existsById("photo-1")) {
            Photo p = new Photo("photo-1", "Amanecer en los Acantilados", "Paisajismo", 130.0,
                    "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1200&q=85",
                    "Luz dorada matutina sobre la costa marítima de Mar del Plata.",
                    "75 x 50 cm · Impresión Fine Art",
                    "Sony Alpha 7 IV · FE 24-70mm f/2.8 GM II · f/8.0 · 1/250s · ISO 100",
                    "Sony Alpha 7 IV", "FE 24-70mm f/2.8 GM II", "f/8.0", "1/250s", "ISO 100",
                    true, "Mar del Plata", true);
            photoRepository.save(p);
        }
    }

    @Test
    @DisplayName("CORS Dinámico: Debe permitir preflight OPTIONS desde puertos dinámicos como http://localhost:59522")
    void testDynamicCorsOnArbitraryLocalhostPort() throws Exception {
        mockMvc.perform(options("/api/photos")
                        .header("Origin", "http://localhost:59522")
                        .header("Access-Control-Request-Method", "PUT")
                        .header("Access-Control-Request-Headers", "Authorization,Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:59522"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    @DisplayName("CORS Dinámico: Debe permitir preflight OPTIONS desde http://127.0.0.1:4200")
    void testDynamicCorsOn127001() throws Exception {
        mockMvc.perform(options("/api/profile")
                        .header("Origin", "http://127.0.0.1:4200")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://127.0.0.1:4200"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    @DisplayName("Edición de Foto: PUT /api/photos/{id} sin JWT debe rechazar con 401 Unauthorized")
    void testPutPhotoWithoutJwtUnauthorized() throws Exception {
        mockMvc.perform(put("/api/photos/photo-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Foto Modificada Sin Auth\",\"price\":999.0}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Edición de Foto: PUT /api/photos/{id} con JWT ADMIN debe actualizar correctamente (200 OK)")
    void testPutPhotoWithAdminJwtSuccess() throws Exception {
        String updateJson = """
                {
                    "title": "Amanecer en los Acantilados (Editado)",
                    "category": "Paisajismo",
                    "price": 199.99,
                    "description": "Nueva descripción actualizada con éxito"
                }
                """;

        mockMvc.perform(put("/api/photos/photo-1")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("photo-1"))
                .andExpect(jsonPath("$.title").value("Amanecer en los Acantilados (Editado)"))
                .andExpect(jsonPath("$.price").value(199.99));
    }

    @Test
    @DisplayName("CMS SiteContent: GET /api/site-content es público y PUT con JWT ADMIN persiste los textos")
    void testSiteContentGetAndPutSuccess() throws Exception {
        // GET público
        mockMvc.perform(get("/api/site-content"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.heroTitle").exists());

        // PUT sin JWT debe rechazar con 401
        mockMvc.perform(put("/api/site-content")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"heroTitle\":\"The World, Reimagined\"}"))
                .andExpect(status().isUnauthorized());

        // PUT con JWT ADMIN debe actualizar y persistir
        String updateJson = """
                {
                    "heroTitle": "The World, Reimagined",
                    "brandName": "Dennis Wanderlight Studio",
                    "heroSubtitle": "Exploraciones fotográficas sin filtros"
                }
                """;

        mockMvc.perform(put("/api/site-content")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.heroTitle").value("The World, Reimagined"))
                .andExpect(jsonPath("$.brandName").value("Dennis Wanderlight Studio"));
    }

    @Test
    @DisplayName("Contacto: POST /api/contact es público y GET /api/contact con JWT ADMIN lista mensajes")
    void testContactSubmissionAndListing() throws Exception {
        String contactJson = """
                {
                    "name": "Alex Mercer",
                    "email": "alex@example.com",
                    "subject": "Expedición a los Andes",
                    "message": "Hola Dennis, nos encantaría coordinar una cobertura fotográfica."
                }
                """;

        // Envío público exitoso
        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));

        // GET sin auth debe rechazar
        mockMvc.perform(get("/api/contact"))
                .andExpect(status().isUnauthorized());

        // GET con JWT ADMIN debe listar los mensajes
        mockMvc.perform(get("/api/contact")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("CRUD Perfil: GET /api/profile es público y devuelve los datos del perfil (200 OK)")
    void testProfileGetPublic() throws Exception {
        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Julieta Marateo"))
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    @DisplayName("CRUD Perfil: PUT /api/profile sin JWT debe rechazar con 401 Unauthorized")
    void testProfilePutWithoutJwtUnauthorized() throws Exception {
        mockMvc.perform(put("/api/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"whatsapp\":\"2281999888\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("CRUD Perfil: PUT /api/profile con JWT ADMIN actualiza información de contacto, tags y persiste (200 OK)")
    void testProfilePutWithAdminJwtSuccessAndPersistence() throws Exception {
        String updateJson = """
                {
                    "whatsapp": "2281554433",
                    "email": "contacto@julietamarateo.com",
                    "instagram": "@julieta_oficial",
                    "tags": ["Bodas de Destino", "Moda Editorial", "Retoque Avanzado"]
                }
                """;

        mockMvc.perform(put("/api/profile")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Julieta Marateo"))
                .andExpect(jsonPath("$.whatsapp").value("2281554433"))
                .andExpect(jsonPath("$.email").value("contacto@julietamarateo.com"))
                .andExpect(jsonPath("$.instagram").value("@julieta_oficial"))
                .andExpect(jsonPath("$.tags[0]").value("Bodas de Destino"))
                .andExpect(jsonPath("$.tags[1]").value("Moda Editorial"));

        // Verificar persistencia consultando con GET
        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.whatsapp").value("2281554433"))
                .andExpect(jsonPath("$.email").value("contacto@julietamarateo.com"))
                .andExpect(jsonPath("$.instagram").value("@julieta_oficial"))
                .andExpect(jsonPath("$.tags[0]").value("Bodas de Destino"))
                .andExpect(jsonPath("$.tags[1]").value("Moda Editorial"));
    }

    @Test
    @DisplayName("Sobre Mí / About: Alias /api/about responde en GET público y PUT con JWT ADMIN")
    void testAboutAliasEndpoints() throws Exception {
        // GET público en /api/about
        mockMvc.perform(get("/api/about"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Julieta Marateo"))
                .andExpect(jsonPath("$.tags").isArray());

        // PUT en /api/about con ADMIN
        String updateJson = """
                {
                    "title": "Fotógrafa & Directora Visual",
                    "tags": ["Cobertura XV", "Sesiones Fine Art"]
                }
                """;

        mockMvc.perform(put("/api/about")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Fotógrafa & Directora Visual"))
                .andExpect(jsonPath("$.tags[0]").value("Cobertura XV"));

        // Verificar que GET /api/about refleja la persistencia
        mockMvc.perform(get("/api/about"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Fotógrafa & Directora Visual"))
                .andExpect(jsonPath("$.tags[0]").value("Cobertura XV"));
    }

    @Test
    @DisplayName("Sobre Mí: PATCH /api/profile permite actualización parcial conservando campos existentes")
    void testProfilePatchEndpoint() throws Exception {
        String patchJson = """
                {
                    "location": "Mar del Plata & CABA, Argentina"
                }
                """;

        mockMvc.perform(patch("/api/profile")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("Mar del Plata & CABA, Argentina"))
                .andExpect(jsonPath("$.name").value("Julieta Marateo"));

        // Verificar persistencia
        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("Mar del Plata & CABA, Argentina"));
    }

    @Test
    @DisplayName("Pedidos: POST /api/orders es público, calcula totales y persiste la orden en SQLite (201 Created)")
    void testCreateOrderPublicSuccess() throws Exception {
        String orderJson = """
                {
                    "customerName": "Carlos Mendoza",
                    "customerContact": "carlos@mendoza.com",
                    "notes": "Entrega en zona Güemes",
                    "items": [
                        {
                            "photoId": "photo-1",
                            "quantity": 2
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerName").value("Carlos Mendoza"))
                .andExpect(jsonPath("$.totalItems").value(2))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("Pedidos: GET /api/orders con JWT ADMIN lista los pedidos (200 OK)")
    void testGetOrdersAdminSuccess() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Multipart Fotos: POST /api/photos con archivo físico y JWT ADMIN crea la foto exitosamente (201 Created)")
    void testCreatePhotoMultipartSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-photo.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );

        mockMvc.perform(multipart("/api/photos")
                        .file(file)
                        .param("title", "Foto de Playa con Archivo")
                        .param("category", "Paisajismo")
                        .param("price", "185.0")
                        .param("dimensions", "75 x 50 cm · Fine Art")
                        .param("technicalSheet", "Sony A7IV · 24-70mm")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Foto de Playa con Archivo"))
                .andExpect(jsonPath("$.imageUrl").value(org.hamcrest.Matchers.containsString("res.cloudinary.com")))
                .andExpect(jsonPath("$.thumbnailUrl").value(org.hamcrest.Matchers.containsString("c_scale,w_480")));
    }

    @Test
    @DisplayName("Multipart Fotos: PUT /api/photos/{id} con archivo físico y JWT ADMIN actualiza la foto (200 OK)")
    void testUpdatePhotoMultipartSuccess() throws Exception {
        MockMultipartFile newFile = new MockMultipartFile(
                "file",
                "updated-photo.jpg",
                "image/jpeg",
                "updated image content".getBytes()
        );

        mockMvc.perform(multipart("/api/photos/photo-1")
                        .file(newFile)
                        .param("title", "Amanecer con Nuevo Archivo")
                        .param("category", "Paisajismo")
                        .param("price", "210.0")
                        .header("Authorization", "Bearer " + adminToken)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("photo-1"))
                .andExpect(jsonPath("$.title").value("Amanecer con Nuevo Archivo"))
                .andExpect(jsonPath("$.price").value(210.0))
                .andExpect(jsonPath("$.imageUrl").value(org.hamcrest.Matchers.containsString("res.cloudinary.com")));
    }

    @Test
    @DisplayName("Multipart Perfil: PUT /api/profile con archivo físico y JWT ADMIN actualiza foto y perfil (200 OK)")
    void testUpdateProfileMultipartSuccess() throws Exception {
        MockMultipartFile profileFile = new MockMultipartFile(
                "file",
                "avatar.jpg",
                "image/jpeg",
                "avatar image bytes".getBytes()
        );

        mockMvc.perform(multipart("/api/profile")
                        .file(profileFile)
                        .param("name", "Julieta Marateo Editado")
                        .param("title", "Fotógrafa Profesional")
                        .param("location", "Mar del Plata, Argentina")
                        .param("bio", "Nueva biografía profesional actualizada.")
                        .param("whatsapp", "2281311917")
                        .header("Authorization", "Bearer " + adminToken)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Julieta Marateo Editado"))
                .andExpect(jsonPath("$.title").value("Fotógrafa Profesional"))
                .andExpect(jsonPath("$.imageUrl").value(org.hamcrest.Matchers.containsString("res.cloudinary.com")));
    }

    @Test
    @DisplayName("Admin Photos Multipart: POST /api/admin/photos con archivo físico y JWT ADMIN crea la foto y persiste secure_url")
    void testCreateAdminPhotoMultipartSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "admin-test.jpg",
                "image/jpeg",
                "admin image content".getBytes()
        );

        mockMvc.perform(multipart("/api/admin/photos")
                        .file(file)
                        .param("title", "Foto desde Admin")
                        .param("category", "Eventos")
                        .param("price", "250.0")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Foto desde Admin"))
                .andExpect(jsonPath("$.imageUrl").value(org.hamcrest.Matchers.containsString("res.cloudinary.com")));
    }

    @Test
    @DisplayName("Optimización I/O: GET /uploads/ debe incluir Cache-Control inmutable y soporte ETag")
    void testUploadsCacheControlAndEtagHeaders() throws Exception {
        java.nio.file.Path uploadsDir = java.nio.file.Paths.get("uploads").toAbsolutePath();
        java.nio.file.Files.createDirectories(uploadsDir);
        java.nio.file.Path testFile = uploadsDir.resolve("cache-test.jpg");
        java.nio.file.Files.write(testFile, "cache image content".getBytes());

        try {
            mockMvc.perform(get("/uploads/cache-test.jpg"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("public")))
                    .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("max-age=31536000")))
                    .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("immutable")))
                    .andExpect(header().exists("ETag"));
        } finally {
            java.nio.file.Files.deleteIfExists(testFile);
        }
    }

    @Test
    @DisplayName("Seguridad: Endpoints de Mercado Pago son accesibles públicamente sin autenticación previa")
    void testMercadoPagoEndpointsPublicAccess() throws Exception {
        // Webhook es público y no devuelve 401
        mockMvc.perform(post("/api/mercadopago/webhook"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Álbumes: Consulta pública GET /api/albums retorna lista de álbumes")
    void testGetAlbumsPublic() throws Exception {
        mockMvc.perform(get("/api/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Álbumes: Creación, actualización y eliminación CRUD con seguridad ADMIN")
    void testAlbumsCrudFlow() throws Exception {
        String newAlbumJson = """
            {
                "id": "test-album-xv",
                "name": "Test Álbum XV",
                "category": "Test Álbum XV",
                "description": "Descripción para test de álbumes",
                "coverImage": "https://example.com/cover.jpg",
                "displayOrder": 10
            }
        """;

        // Sin token debe ser rechazado
        mockMvc.perform(post("/api/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newAlbumJson))
                .andExpect(status().isUnauthorized());

        // Con token ADMIN se crea exitosamente
        mockMvc.perform(post("/api/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newAlbumJson)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("test-album-xv"))
                .andExpect(jsonPath("$.name").value("Test Álbum XV"));

        // Actualizar álbum
        String updateJson = """
            {
                "name": "Test Álbum XV Actualizado",
                "description": "Nueva descripción actualizada",
                "coverImage": "https://example.com/cover-updated.jpg"
            }
        """;
        mockMvc.perform(put("/api/albums/test-album-xv")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Álbum XV Actualizado"));

        // Agregar una foto individual al álbum
        String newPhotoJson = """
            {
                "imageUrl": "https://example.com/photo-to-delete.jpg",
                "caption": "Foto a borrar",
                "orientation": "portrait"
            }
        """;
        String photoResp = mockMvc.perform(post("/api/albums/test-album-xv/photos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newPhotoJson)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/photo-to-delete.jpg"))
                .andReturn().getResponse().getContentAsString();

        // Extraer id de la foto agregada
        com.fasterxml.jackson.databind.JsonNode photoNode = objectMapper.readTree(photoResp);
        String createdPhotoId = photoNode.get("id").asText();

        // Intento de eliminación sin token debe ser rechazado (401)
        mockMvc.perform(delete("/api/albums/test-album-xv/photos/" + createdPhotoId))
                .andExpect(status().isUnauthorized());

        // Eliminación de foto individual con token ADMIN (204 No Content)
        mockMvc.perform(delete("/api/albums/test-album-xv/photos/" + createdPhotoId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Verificar que la foto ya no esté en el álbum
        mockMvc.perform(get("/api/albums/test-album-xv"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photos[?(@.id == '" + createdPhotoId + "')]").doesNotExist());

        // Eliminar álbum
        mockMvc.perform(delete("/api/albums/test-album-xv")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Foto de Portada: GET público y PUT protegido para actualizar portada Hero")
    void testCoverPhotoFlow() throws Exception {
        // GET público
        mockMvc.perform(get("/api/cover-photo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrl").exists());

        // PUT sin token debe ser rechazado
        String updateCoverJson = """
            {
                "photoId": "photo-1",
                "imageUrl": "https://example.com/nueva-portada.jpg",
                "title": "Portada Hero Test",
                "category": "Paisajismo"
            }
        """;
        mockMvc.perform(put("/api/cover-photo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateCoverJson))
                .andExpect(status().isUnauthorized());

        // PUT con token ADMIN se actualiza exitosamente
        mockMvc.perform(put("/api/cover-photo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateCoverJson))
                .andExpect(status().isUnauthorized());

        // PUT con token ADMIN se actualiza exitosamente
        mockMvc.perform(put("/api/cover-photo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateCoverJson)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/nueva-portada.jpg"))
                .andExpect(jsonPath("$.title").value("Portada Hero Test"));
    }

    @Test
    @DisplayName("Lienzo Portfolio: PUT /api/albums/layout persiste coordenadas (xPos, yPos, width, zIndex) y GET /api/albums las devuelve")
    void testUpdateAlbumsLayoutPersistence() throws Exception {
        String layoutJson = """
            [
                {
                    "id": "tokyo-neon-pulse",
                    "xPos": 12.5,
                    "yPos": 24.8,
                    "width": 35.0,
                    "zIndex": 5
                },
                {
                    "id": "the-crimson-sands-of-wadi-rum",
                    "xPos": 48.0,
                    "yPos": 15.2,
                    "width": 30.0,
                    "zIndex": 6
                }
            ]
        """;

        // Sin token debe rechazar
        mockMvc.perform(put("/api/albums/layout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(layoutJson))
                .andExpect(status().isUnauthorized());

        // Con token ADMIN actualiza
        mockMvc.perform(put("/api/albums/layout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(layoutJson)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // Verificar que GET /api/albums devuelve las coordenadas actualizadas
        mockMvc.perform(get("/api/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == 'tokyo-neon-pulse')].xPos").value(12.5))
                .andExpect(jsonPath("$[?(@.id == 'tokyo-neon-pulse')].yPos").value(24.8))
                .andExpect(jsonPath("$[?(@.id == 'tokyo-neon-pulse')].width").value(35.0))
                .andExpect(jsonPath("$[?(@.id == 'tokyo-neon-pulse')].zIndex").value(5))
                .andExpect(jsonPath("$[?(@.id == 'the-crimson-sands-of-wadi-rum')].xPos").value(48.0))
                .andExpect(jsonPath("$[?(@.id == 'the-crimson-sands-of-wadi-rum')].yPos").value(15.2));

        // Verificar también que /api/admin/albums/layout funciona como endpoint alternativo
        mockMvc.perform(put("/api/admin/albums/layout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(layoutJson)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}

