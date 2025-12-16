package com.sistema.estoque_service.mensageria.evento;

import java.math.BigDecimal;

public record PedidoFinalizadoEvent(
        String codigo,
        String nomeCliente,
        Long produtoId,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal total
) {}

