package com.sistema.catalogoservice.catalogo.produto.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProdutoResponse(
		Long id,
		String codigo,
		String nome,
		List<CategoriaHierarquiaResponse> categoriasHierarquia,
		List<CorHierarquiaResponse> coresHierarquia,
		String medidas,
		BigDecimal precoUnitario,
		Boolean ativo,
		String imagemPrincipalUrl,
		String descricao,
		OffsetDateTime criadoEm,
		OffsetDateTime atualizadoEm
) {}
