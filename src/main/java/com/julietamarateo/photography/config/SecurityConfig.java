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

    @Value("${app.cors.allowed-origins:http://localhost:*,http://127.0.0.1:*,https://localhost:*,https://127.0.0.1:*,https://*.vercel.app,https://juli-fotografia-front-oafx.vercel.app,*}")
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
                
                // Endpoints públicos de lectura y autenticación (con y sin prefijo /api)
                .requestMatchers(HttpMethod.POST, "/api/auth/**", "/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/photos/**", "/photos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/site-content/**", "/site-content/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/profile/**", "/profile/**", "/api/about/**", "/about/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/albums/**", "/albums/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cover-photo/**", "/cover-photo/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/services/**", "/services/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/contact", "/contact").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/orders/**", "/orders/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/mercadopago/**", "/mercadopago/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/mercadopago/**", "/mercadopago/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                .requestMatchers("/error").permitAll()
                
                // Endpoints de modificación estrictamente protegidos para el Administrador
                .requestMatchers(HttpMethod.POST, "/api/admin/**", "/admin/**", "/api/photos/**", "/photos/**", "/api/site-content/**", "/site-content/**", "/api/profile/**", "/profile/**", "/api/about/**", "/about/**", "/api/albums/**", "/albums/**", "/api/cover-photo/**", "/cover-photo/**", "/api/services/**", "/services/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/admin/**", "/admin/**", "/api/photos/**", "/photos/**", "/api/site-content/**", "/site-content/**", "/api/profile/**", "/profile/**", "/api/about/**", "/about/**", "/api/albums/**", "/albums/**", "/api/cover-photo/**", "/cover-photo/**", "/api/services/**", "/services/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/admin/**", "/admin/**", "/api/photos/**", "/photos/**", "/api/albums/**", "/albums/**", "/api/contact/**", "/contact/**", "/api/services/**", "/services/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/admin/**", "/admin/**", "/api/orders/**", "/orders/**", "/api/contact/**", "/contact/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/admin/**", "/admin/**", "/api/orders/**", "/orders/**", "/api/profile/**", "/profile/**", "/api/about/**", "/about/**", "/api/contact/**", "/contact/**").hasRole("ADMIN")
                
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
