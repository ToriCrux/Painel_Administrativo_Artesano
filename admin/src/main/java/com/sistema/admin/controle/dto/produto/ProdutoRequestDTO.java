package com.sistema.admin.controle.dto.produto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Set;

public record ProdutoRequestDTO(

        @NotBlank @Size(max = 60)
        String codigo,

        @NotBlank @Size(max = 120)
        String nome,

        @NotBlank @Size(max = 100)  // agora espera nome da categoria
        String categoriaNome,

        Set<Long> corIds,

        @Size(max = 120)
        String medidas,

        @NotNull @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal precoUnitario,

        @NotNull
        Boolean ativo
) {}
