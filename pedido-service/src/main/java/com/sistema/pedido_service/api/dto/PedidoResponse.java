package com.sistema.pedido_service.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PedidoResponse {

    private Long id;
    private String codigo;
    private String nomeCliente;
    private String produto;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal total;
    private StatusPedido status;
}
