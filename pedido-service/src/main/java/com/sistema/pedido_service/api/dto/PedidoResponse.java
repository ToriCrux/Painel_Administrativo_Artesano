package com.sistema.pedido_service.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class PedidoResponse {
    private Long id;
    private String codigo;
    private String nomeCliente;
    private BigDecimal total;
    private StatusPedido status;
    private List<ItemPedidoResponse> itens;
}
