package com.sistema.admin.auth.api.dto;

import java.util.Set;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Boolean ativo,
        Set<String> roles
) {}
