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
        List<CategoriaHierarquiaRequest> categorias, // ✅ estrutura hierárquica

        @NotNull
        @Size(min = 1, message = "Informe pelo menos 1 grupo de cor")
        List<CorGrupoRequest> cores, // ✅ agora suporta N grupos e N subcores

        @Size(max = 120)
        String medidas,

        @NotNull @DecimalMin(value = "0.0")
        BigDecimal precoUnitario,

        @NotNull
        Boolean ativo,

        String descricao
) {}
