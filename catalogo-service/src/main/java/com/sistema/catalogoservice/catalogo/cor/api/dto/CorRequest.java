package com.sistema.catalogoservice.catalogo.cor.api.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record CorRequest(
        @NotBlank @Size(max = 60)
        String nome,

        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Hex inválido")
        String hex,

        Boolean ativo,

        // ✅ Permite criar subcores aninhadas
        List<CorRequest> subcores
) {}
