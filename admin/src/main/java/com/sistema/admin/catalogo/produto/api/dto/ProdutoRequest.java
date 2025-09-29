package com.sistema.admin.catalogo.produto.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Set;

public record ProdutoRequest(

        @NotBlank @Size(max = 60)
        String codigo,

        @NotBlank @Size(max = 120)
        String nome,

        @NotBlank @Size(max = 100)
        String categoriaNome,

        Set<Long> corIds,

        @Size(max = 120)
        String medidas,

        @NotNull @DecimalMin(value = "0.0")
        BigDecimal precoUnitario,

        @NotNull
        Boolean ativo,

        String descricao
) {}
