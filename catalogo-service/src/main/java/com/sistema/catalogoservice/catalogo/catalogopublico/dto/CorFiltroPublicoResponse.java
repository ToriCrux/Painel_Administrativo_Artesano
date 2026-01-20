package com.sistema.catalogoservice.catalogo.catalogopublico.dto;

import java.util.List;

public record CorFiltroPublicoResponse(
        Long id,
        String nome,
        String hex,
        List<SubcorFiltroPublicoResponse> subcores
) {
    public record SubcorFiltroPublicoResponse(
            Long id,
            String nome,
            String hex
    ) {}
}
