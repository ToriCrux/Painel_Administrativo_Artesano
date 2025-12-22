package com.sistema.autenticacao_service.api.dto;

public record TokenResponse(String token) {
    public String getToken() {
        return token;
    }
}
