package com.sistema.catalogoservice.mensageria.evento;

public record ProdutoCriadoEvent(
		Long produtoId,
		String codigo,
		String nome,
		Boolean ativo
) {}
