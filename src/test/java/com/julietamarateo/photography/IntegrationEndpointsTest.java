package com.julietamarateo.photography;

import com.julietamarateo.photography.config.JwtTokenProvider;
import com.julietamarateo.photography.entity.Photo;
import com.julietamarateo.photography.entity.ServiceItem;
import com.julietamarateo.photography.repository.PhotoRepository;
import com.julietamarateo.photography.repository.ProfileRepository;
import com.julietamarateo.photography.repository.ServiceRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ProfileRepository profileRepository;

    private String adminToken;

    @BeforeEach
    void setUp() {
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

        if (!serviceRepository.existsById("serv-1")) {
            ServiceItem s = new ServiceItem(
                    "serv-1",
                    "Casamientos",
                    "Cobertura fotográfica integral y sensible para el día de tu boda.",
                    "https://images.unsplash.com/photo-1519741497674-611481863552?auto=format&fit=crop&w=800&q=80",
                    List.of("Preparativos", "Ceremonia", "Fiesta"),
                    "https://wa.me/5492281311917",
                    450.0
            );
            serviceRepository.save(s);
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
    @DisplayName("Edición de Servicio: PUT /api/services/{id} sin JWT debe rechazar con 401 Unauthorized")
    void testPutServiceWithoutJwtUnauthorized() throws Exception {
        mockMvc.perform(put("/api/services/serv-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Servicio No Autorizado\",\"price\":999.0}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Edición de Servicio: PUT /api/services/{id} con JWT ADMIN debe actualizar correctamente (200 OK)")
    void testPutServiceWithAdminJwtSuccess() throws Exception {
        String updateJson = """
                {
                    "title": "Casamientos Premium 2026",
                    "price": 550.0,
                    "description": "Cobertura ampliada de bodas y fiestas"
                }
                """;

        mockMvc.perform(put("/api/services/serv-1")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("serv-1"))
                .andExpect(jsonPath("$.title").value("Casamientos Premium 2026"));
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
    @DisplayName("CRUD Perfil: PUT /api/profile con JWT ADMIN actualiza información de contacto y persiste (200 OK)")
    void testProfilePutWithAdminJwtSuccessAndPersistence() throws Exception {
        String updateJson = """
                {
                    "whatsapp": "2281554433",
                    "email": "contacto@julietamarateo.com",
                    "instagram": "@julieta_oficial"
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
                .andExpect(jsonPath("$.instagram").value("@julieta_oficial"));

        // Verificar persistencia consultando con GET
        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.whatsapp").value("2281554433"))
                .andExpect(jsonPath("$.email").value("contacto@julietamarateo.com"))
                .andExpect(jsonPath("$.instagram").value("@julieta_oficial"));
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
                .andExpect(jsonPath("$.imageUrl").value(org.hamcrest.Matchers.containsString("/uploads/photos/")));
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
                .andExpect(jsonPath("$.imageUrl").value(org.hamcrest.Matchers.containsString("/uploads/photos/")));
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
                .andExpect(jsonPath("$.imageUrl").value(org.hamcrest.Matchers.containsString("/uploads/profile/")));
    }

    @Test
    @DisplayName("Optimización I/O: GET /uploads/ debe incluir Cache-Control inmutable y soporte ETag")
    void testUploadsCacheControlAndEtagHeaders() throws Exception {
        // Primero subir un archivo para tener un recurso real en /uploads/
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cache-test.jpg",
                "image/jpeg",
                "cache image content".getBytes()
        );

        String responseContent = mockMvc.perform(multipart("/api/photos")
                        .file(file)
                        .param("title", "Foto para Test Cache")
                        .param("category", "Paisajismo")
                        .param("price", "120.0")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.imageUrl").exists())
                .andExpect(jsonPath("$.thumbnailUrl").exists())
                .andReturn().getResponse().getContentAsString();

        String imageUrl = com.jayway.jsonpath.JsonPath.read(responseContent, "$.imageUrl");

        // Consultar el recurso estático y verificar cabeceras HTTP de caché y ETag
        mockMvc.perform(get(imageUrl))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("public")))
                .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("max-age=31536000")))
                .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("immutable")))
                .andExpect(header().exists("ETag"));
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
                        .content(updateCoverJson)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/nueva-portada.jpg"))
                .andExpect(jsonPath("$.title").value("Portada Hero Test"));
    }
}

