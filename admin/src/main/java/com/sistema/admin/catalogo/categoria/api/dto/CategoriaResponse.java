package com.sistema.admin.catalogo.categoria.api.dto;

import java.time.OffsetDateTime;

public record CategoriaResponse(
        Long id,
        String nome,
        Boolean ativo,
        OffsetDateTime criadoEm,
        OffsetDateTime atualizadoEm
) {}

