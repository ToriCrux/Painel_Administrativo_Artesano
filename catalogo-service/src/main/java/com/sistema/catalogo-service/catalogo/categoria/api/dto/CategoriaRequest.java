package com.sistema.admin.catalogo.categoria.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequest(
        @NotBlank String nome,
        boolean ativo
) {}

