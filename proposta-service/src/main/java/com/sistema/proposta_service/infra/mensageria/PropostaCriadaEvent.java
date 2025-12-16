package com.sistema.proposta_service.infra.mensageria;

import com.sistema.proposta_service.dominio.Proposta;

import java.math.BigDecimal;
import java.util.List;

public record PropostaCriadaEvent(
        String codigoProposta,
        String nomeCliente,
        List<ItemPedidoDTO> produtos
) {
    public record ItemPedidoDTO(
            String codigoProduto,
            String nomeProduto,
            Integer quantidade,
            BigDecimal precoUnitario
    ) {}

    public static PropostaCriadaEvent from(Proposta proposta) {
        var itens = proposta.getProdutos().stream()
                .map(p -> new ItemPedidoDTO(
                        p.getCodigoProduto(),
                        p.getNomeProduto(),
                        p.getQuantidade(),
                        p.getPrecoUnitario()
                ))
                .toList();

        return new PropostaCriadaEvent(
                proposta.getCodigo(),
                proposta.getCliente().getNome(),
                itens
        );
    }
}
