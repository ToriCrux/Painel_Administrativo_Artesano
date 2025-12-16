package com.sistema.pedido_service.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PedidoRequest {

    @NotBlank
    private String codigo;

    @NotBlank
    private String nomeCliente;

    @NotBlank
    private String produto;

    @NotNull
    @Min(1)
    private Integer quantidade;

    @NotNull
    private BigDecimal precoUnitario;
}
