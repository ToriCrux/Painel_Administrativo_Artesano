package com.sistema.proposta_service.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProdutoPropostaDTO {

    @NotBlank
    private String codigoProduto;

    @NotBlank
    private String nomeProduto;

    @NotNull
    @Min(1)
    private Integer quantidade;

    @NotNull
    private BigDecimal precoUnitario;
}
