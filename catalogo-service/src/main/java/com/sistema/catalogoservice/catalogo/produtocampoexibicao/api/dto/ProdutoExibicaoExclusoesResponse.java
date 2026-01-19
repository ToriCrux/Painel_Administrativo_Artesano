package com.sistema.catalogoservice.catalogo.produtocampoexibicao.api.dto;

import java.util.List;

public record ProdutoExibicaoExclusoesResponse(
        List<Long> categoriaIds,
        List<Long> subcategoriaIds,
        List<Long> itemIds,
        List<Long> corGrupoIds,
        List<Long> subcorIds,
        List<String> detalhesChaves
) {}
