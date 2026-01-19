package com.sistema.catalogoservice.catalogo.produto.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

public record ProdutoRequest(

        @NotBlank @Size(max = 60)
        String codigo,

        @NotBlank @Size(max = 120)
        String nome,

        // ✅ Sempre presente (no seu modelo atual)
        // [] => limpa / [...] => substitui
        @NotNull
        @Valid
        List<CategoriaHierarquiaRequest> categorias,

        // ✅ Opcional: se vier null => não altera (se você enviar)
        // [] => limpa / [...] => substitui
        @Valid
        List<CorGrupoRequest> cores,

        // ✅ Opcional: null => não altera / {} => limpa / {...} => substitui
        Map<String, String> detalhesTecnicos,

        @NotNull @DecimalMin(value = "0.0")
        BigDecimal precoUnitario,

        @NotNull
        Boolean ativo,

        String descricao
) {}
