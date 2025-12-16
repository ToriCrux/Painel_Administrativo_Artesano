package com.sistema.pedido_service.infra.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class BearerTokenProvider {

    private final String serviceJwt;

    public BearerTokenProvider(@Value("${auth.service.token:}") String serviceJwt) {
        this.serviceJwt = serviceJwt;
    }

    public String resolveBearerToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            String tokenValue = jwtAuth.getToken().getTokenValue();
            if (StringUtils.hasText(tokenValue)) {
                return "Bearer " + tokenValue; // Adicione o prefixo "Bearer "
            }
        }

        if (StringUtils.hasText(serviceJwt)) {
            return "Bearer " + serviceJwt; // Adicione o prefixo "Bearer "
        }

        return null;
    }
}