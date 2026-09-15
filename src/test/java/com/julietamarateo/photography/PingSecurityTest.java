package com.julietamarateo.photography;

import com.julietamarateo.photography.config.JwtAuthenticationFilter;
import com.julietamarateo.photography.config.JwtTokenProvider;
import com.julietamarateo.photography.config.SecurityConfig;
import com.julietamarateo.photography.controller.PingController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PingController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtTokenProvider.class})
public class PingSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Security: GET /ping returns 200 OK without authentication")
    void testGetPingPermitAll() throws Exception {
        mockMvc.perform(get("/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("Security: HEAD /ping returns 200 OK without authentication (UptimeRobot support)")
    void testHeadPingPermitAll() throws Exception {
        mockMvc.perform(head("/ping"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Security: GET /api/ping returns 200 OK without authentication")
    void testGetApiPingPermitAll() throws Exception {
        mockMvc.perform(get("/api/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("Security: HEAD /api/ping returns 200 OK without authentication")
    void testHeadApiPingPermitAll() throws Exception {
        mockMvc.perform(head("/api/ping"))
                .andExpect(status().isOk());
    }
}
