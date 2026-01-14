package com.sistema.catalogoservice.catalogo.produto.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record ProdutoRequest(

        @NotBlank @Size(max = 60)
        String codigo,

        @NotBlank @Size(max = 120)
        String nome,

        @NotNull
        List<CategoriaHierarquiaRequest> categorias, // ✅ nova estrutura hierárquica

        @NotBlank
        String corNome,

        List<String> subcorNomes,

        @Size(max = 120)
        String medidas,

        @NotNull @DecimalMin(value = "0.0")
        BigDecimal precoUnitario,

        @NotNull
        Boolean ativo,

        String descricao
) {}
