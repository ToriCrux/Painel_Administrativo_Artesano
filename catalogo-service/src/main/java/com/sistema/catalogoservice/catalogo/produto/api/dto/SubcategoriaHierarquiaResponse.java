package com.sistema.catalogoservice.catalogo.produto.api.dto;

import java.util.List;

public record SubcategoriaHierarquiaResponse(
        Long id,
        String nome,
        List<ItemHierarquiaResponse> itens
) {}
