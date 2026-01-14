package com.sistema.catalogoservice.catalogo.categoria.api.dto;

import java.time.OffsetDateTime;

public record ItemCategoriaResponse(
        Long id,
        String nome,
        Boolean ativo,
        OffsetDateTime criadoEm,
        OffsetDateTime atualizadoEm
) {}
