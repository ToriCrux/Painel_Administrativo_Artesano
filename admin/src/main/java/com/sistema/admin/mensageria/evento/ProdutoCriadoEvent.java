package com.sistema.admin.mensageria.evento;

public record ProdutoCriadoEvent(
		Long produtoId,
		String codigo,
		String nome,
		Boolean ativo
) {}
