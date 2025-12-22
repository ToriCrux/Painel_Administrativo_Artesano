package com.sistema.autenticacao_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        // ✅ permite o front-end local do Next.js
                        .allowedOriginPatterns("http://localhost:3000")
                        // ✅ métodos HTTP permitidos
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        // ✅ cabeçalhos customizados (ex: Authorization, Content-Type)
                        .allowedHeaders("*")
                        // ✅ necessário para envio de cookies JWT
                        .allowCredentials(true)
                        // ✅ evita cache de preflight (opcional, útil no dev)
                        .maxAge(3600);
            }
        };
    }
}
