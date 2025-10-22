package com.sistema.autenticacao_service.api.dto;

import java.util.Set;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Boolean ativo,
        String role
) {}
