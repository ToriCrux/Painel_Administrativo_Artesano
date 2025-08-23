package com.sistema.admin.controle.dto;

import java.util.Set;

public record UsuarioRespostaDTO(
        Long id,
        String nome,
        String email,
        Boolean ativo,
        Set<String> roles
) {}
