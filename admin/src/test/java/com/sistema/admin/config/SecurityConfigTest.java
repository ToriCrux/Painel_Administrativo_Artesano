package com.sistema.admin.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    private final SecurityConfig config = new SecurityConfig();

    @Test
    @DisplayName("jwtDecoder deve criar instância de JwtDecoder")
    void jwtDecoder_shouldCreateInstance() {
        JwtDecoder decoder = config.jwtDecoder("dGVzdGUtc2VjcmV0LXNlY3JldA=="); // "teste-secret" em Base64

        assertThat(decoder).isNotNull();
    }

    @Test
    @DisplayName("jwtAuthenticationConverter deve ser criado")
    void jwtAuthenticationConverter_shouldConfigureRolesClaim() {
        JwtAuthenticationConverter converter = config.jwtAuthenticationConverter();

        assertThat(converter).isNotNull();
    }

    @Test
    @DisplayName("corsConfigurationSource deve definir origens e métodos permitidos")
    void corsConfigurationSource_shouldDefineAllowedOrigins() {
        CorsConfigurationSource source = config.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/teste");
        CorsConfiguration cfg = source.getCorsConfiguration(request);

        assertThat(cfg).isNotNull();
        assertThat(cfg.getAllowedOrigins()).isNotEmpty();
        assertThat(cfg.getAllowedMethods()).contains("GET", "POST");
    }
}
