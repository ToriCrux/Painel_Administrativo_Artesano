package com.sistema.catalogoservice.catalogo.produtocampoexibicao.api.dto;

import java.util.Map;

public record ProdutoExibicaoDetalhadaResponse(
        Long produtoId,
        Map<String, Boolean> configuracoes,
        ProdutoExibicaoExclusoesResponse exclusoes
) {}
