package com.sistema.estoque_service.mensageria.evento;

public record ProdutoCriadoEvent(
		Long produtoId,
		String codigo,
		String nome,
		Boolean ativo
) {}
