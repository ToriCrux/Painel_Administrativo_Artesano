package com.sistema.catalogoservice.catalogo.produto.api.dto;

import java.util.List;

public record CorHierarquiaResponse(
        Long id,
        String nome,
        String hex,
        List<SubcorResponse> subcores
) {}
