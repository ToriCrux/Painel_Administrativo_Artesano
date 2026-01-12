package com.sistema.catalogoservice.catalogo.produto.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Set;

public record ProdutoRequest(

        @NotBlank @Size(max = 60)
        String codigo,

        @NotBlank @Size(max = 120)
        String nome,

        @NotNull
        Long categoriaId, // ✅ Categoria obrigatória

        Long subcategoriaId, // ✅ Subcategoria opcional

        Set<Long> corIds,

        @Size(max = 120)
        String medidas,

        @NotNull @DecimalMin(value = "0.0")
        BigDecimal precoUnitario,

        @NotNull
        Boolean ativo,

        String descricao
) {}
