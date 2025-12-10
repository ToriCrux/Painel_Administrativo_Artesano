package com.sistema.catalogoservice.catalogo.produtocampoexibicao.api.dto;

import java.util.Map;

public record ProdutoCampoExibicaoResponse(
        Long produtoId,
        Map<String, Boolean> configuracoes
) {}
