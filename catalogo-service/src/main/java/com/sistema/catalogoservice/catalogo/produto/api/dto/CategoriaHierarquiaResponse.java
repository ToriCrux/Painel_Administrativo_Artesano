package com.sistema.catalogoservice.catalogo.produto.api.dto;

import java.util.List;

public record CategoriaHierarquiaResponse(
        Long id,
        String nome,
        List<SubcategoriaHierarquiaResponse> subcategorias
) {}
