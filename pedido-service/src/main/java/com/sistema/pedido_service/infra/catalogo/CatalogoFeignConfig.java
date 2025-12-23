package com.sistema.pedido_service.infra.catalogo;

import com.sistema.pedido_service.infra.security.BearerTokenProvider;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration; // Import essencial
import org.springframework.http.HttpHeaders;

@Configuration // Agora o Spring gerencia este Bean corretamente
public class CatalogoFeignConfig {

    @Bean
    public RequestInterceptor authRequestInterceptor(BearerTokenProvider bearerTokenProvider) {
        return template -> {
            String bearer = bearerTokenProvider.resolveBearerToken();
            if (bearer != null && !bearer.isBlank()) {
                template.header(HttpHeaders.AUTHORIZATION, bearer);
                // Log opcional para debug (remover em produção)
                System.out.println("DEBUG: Feign enviando Header: " + bearer);
            }
        };
    }
}