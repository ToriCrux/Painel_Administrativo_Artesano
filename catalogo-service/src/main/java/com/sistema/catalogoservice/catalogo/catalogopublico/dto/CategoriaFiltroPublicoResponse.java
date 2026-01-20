package com.sistema.catalogoservice.catalogo.catalogopublico.dto;

import java.util.List;

public record CategoriaFiltroPublicoResponse(
        Long id,
        String nome,
        List<SubcategoriaFiltroPublicoResponse> subcategorias
) {
    public record SubcategoriaFiltroPublicoResponse(
            Long id,
            String nome,
            List<ItemFiltroPublicoResponse> itens
    ) {}

    public record ItemFiltroPublicoResponse(
            Long id,
            String nome
    ) {}
}
