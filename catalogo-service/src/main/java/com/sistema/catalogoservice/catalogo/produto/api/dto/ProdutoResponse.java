package com.sistema.catalogoservice.catalogo.produto.api.dto;

import com.sistema.catalogoservice.catalogo.categoria.api.dto.CategoriaResponse;
import com.sistema.catalogoservice.catalogo.cor.api.dto.CorResponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

public record ProdutoResponse(
		Long id,
		String codigo,
		String nome,
		CategoriaResponse categoria,
		CategoriaResponse subcategoria,
		Set<CorResponse> cores,
		String medidas,
		BigDecimal precoUnitario,
		Boolean ativo,
		String imagemPrincipalUrl,
		String descricao,
		OffsetDateTime criadoEm,
		OffsetDateTime atualizadoEm
) {}
