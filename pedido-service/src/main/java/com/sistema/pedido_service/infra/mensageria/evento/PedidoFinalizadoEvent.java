package com.sistema.pedido_service.infra.mensageria.evento;

import java.math.BigDecimal;

public record PedidoFinalizadoEvent(
        String codigo,
        String nomeCliente,
        Long produtoId,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal total
) {}
