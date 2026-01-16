package com.sistema.catalogoservice.catalogo.cor.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CorRequest(
        Long id, // ✅ opcional (para merge). No print pode vir null.

        @NotBlank @Size(max = 60)
        String nome,

        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Hex inválido")
        String hex,

        Boolean ativo,

        List<CorRequest> subcores
) {}
