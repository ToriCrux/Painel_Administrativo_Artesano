package com.sistema.catalogoservice.catalogo.categoria.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ItemCategoriaRequest(
        @NotBlank String nome,
        boolean ativo
) {}
