package com.sistema.catalogoservice.catalogo.categoria.api.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record CategoriaResponse(
        Long id,
        String nome,
        Boolean ativo,
        OffsetDateTime criadoEm,
        OffsetDateTime atualizadoEm,
        List<CategoriaResponse> subcategorias
) {}
