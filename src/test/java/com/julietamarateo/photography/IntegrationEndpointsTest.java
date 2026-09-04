package com.julietamarateo.photography;

import com.julietamarateo.photography.config.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = jwtTokenProvider.generateToken("julietamarateo4@gmail.com", "ROLE_ADMIN");
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
                .andExpect(jsonPath("$.title").value("Casamientos Premium 2026"))
                .andExpect(jsonPath("$.price").value(550.0));
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
}
