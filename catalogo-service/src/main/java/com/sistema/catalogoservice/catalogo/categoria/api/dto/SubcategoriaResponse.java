package com.sistema.catalogoservice.catalogo.categoria.api.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record SubcategoriaResponse(
        Long id,
        String nome,
        Boolean ativo,
        OffsetDateTime criadoEm,
        OffsetDateTime atualizadoEm,
        List<ItemCategoriaResponse> itens
) {}
