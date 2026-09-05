package com.julietamarateo.photography.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${app.cors.allowed-origins:http://localhost:*,http://127.0.0.1:*,https://localhost:*,https://127.0.0.1:*,*}")
    private String allowedOrigins;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\":\"No autorizado\",\"message\":\"Token JWT ausente o inválido\"}");
                })
            )
            .authorizeHttpRequests(auth -> auth
                // Peticiones de preflight CORS siempre permitidas
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Endpoints públicos
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/photos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/site-content/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/profile/**", "/api/about/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/albums/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cover-photo/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/contact").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/orders").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/orders/*/preference").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/mercadopago/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/mercadopago/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                .requestMatchers("/error").permitAll()
                
                // Endpoints de modificación estrictamente protegidos para el Administrador
                .requestMatchers(HttpMethod.POST, "/api/photos/**", "/api/site-content/**", "/api/profile/**", "/api/about/**", "/api/albums/**", "/api/cover-photo/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/photos/**", "/api/site-content/**", "/api/profile/**", "/api/about/**", "/api/albums/**", "/api/cover-photo/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/photos/**", "/api/albums/**", "/api/contact/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/orders/**", "/api/contact/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/orders/**", "/api/profile/**", "/api/about/**", "/api/contact/**").hasRole("ADMIN")
                
                // Cualquier otra solicitud requiere autenticación
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        // allowedOriginPatterns permite http://localhost:* y * con allowCredentials=true
        configuration.setAllowedOriginPatterns(origins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
