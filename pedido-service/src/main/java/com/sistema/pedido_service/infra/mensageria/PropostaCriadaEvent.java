package com.sistema.pedido_service.infra.mensageria;

import java.math.BigDecimal;
import java.util.List;

public record PropostaCriadaEvent(
        String codigoProposta,
        String nomeCliente,
        List<ItemPropostaEvent> produtos
) {
    public record ItemPropostaEvent(
            String codigoProduto,
            String nomeProduto,
            Integer quantidade,
            BigDecimal precoUnitario
    ) {}
}
