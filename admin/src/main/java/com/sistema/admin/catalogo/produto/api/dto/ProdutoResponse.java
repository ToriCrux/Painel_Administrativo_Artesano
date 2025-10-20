package com.sistema.admin.catalogo.produto.api.dto;

import com.sistema.admin.catalogo.categoria.api.dto.CategoriaResponse;
import com.sistema.admin.catalogo.cor.api.dto.CorResponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

public record ProdutoResponse(
        Long id,
        String codigo,
        String nome,
        CategoriaResponse categoria,
        Set<CorResponse> cores,
        String medidas,
        BigDecimal precoUnitario,
        Boolean ativo,
        OffsetDateTime criadoEm,
        OffsetDateTime atualizadoEm,

        String descricao
) {}
