package com.julietamarateo.photography;

import com.julietamarateo.photography.controller.PingController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PingControllerTest {

    private MockMvc mockMvcWithFilter;
    private MockMvc mockMvcDirect;

    @BeforeEach
    void setUp() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:*", "https://*.vercel.app", "https://juli-fotografia-front.vercel.app", "*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        mockMvcWithFilter = MockMvcBuilders.standaloneSetup(new PingController())
                .addFilters(new CorsFilter(source))
                .build();

        mockMvcDirect = MockMvcBuilders.standaloneSetup(new PingController()).build();
    }

    @Test
    @DisplayName("GET /ping should return 200 OK with UP status")
    void testPingEndpoint() throws Exception {
        mockMvcWithFilter.perform(get("/ping")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.message").value("pong"));
    }

    @Test
    @DisplayName("GET /api/ping should return 200 OK with UP status")
    void testApiPingEndpoint() throws Exception {
        mockMvcWithFilter.perform(get("/api/ping")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.message").value("pong"));
    }

    @Test
    @DisplayName("GET /ping with Accept: text/plain returns pong")
    void testPingAcceptTextPlain() throws Exception {
        mockMvcWithFilter.perform(get("/ping")
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }

    @Test
    @DisplayName("CORS Preflight OPTIONS /ping from Vercel front")
    void testPreflightOptionsFromVercelFront() throws Exception {
        mockMvcWithFilter.perform(options("/ping")
                        .header(HttpHeaders.ORIGIN, "https://juli-fotografia-front.vercel.app")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://juli-fotografia-front.vercel.app"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    @DisplayName("CORS Preflight OPTIONS /ping from localhost:4200")
    void testPreflightOptionsFromLocalhost() throws Exception {
        mockMvcWithFilter.perform(options("/ping")
                        .header(HttpHeaders.ORIGIN, "http://localhost:4200")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    @DisplayName("CORS GET /ping from Vercel front")
    void testGetPingFromVercelFront() throws Exception {
        mockMvcWithFilter.perform(get("/ping")
                        .header(HttpHeaders.ORIGIN, "https://juli-fotografia-front.vercel.app")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://juli-fotografia-front.vercel.app"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"))
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("Direct MockMvc without filter respects @CrossOrigin(originPatterns = '*') on GET")
    void testDirectCrossOriginAnnotation() throws Exception {
        mockMvcDirect.perform(get("/ping")
                        .header(HttpHeaders.ORIGIN, "https://juli-fotografia-front.vercel.app")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://juli-fotografia-front.vercel.app"))
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
