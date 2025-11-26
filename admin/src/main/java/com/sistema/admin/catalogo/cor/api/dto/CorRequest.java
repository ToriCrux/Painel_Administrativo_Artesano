package com.sistema.admin.catalogo.cor.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CorRequest(
        @NotBlank @Size(max = 60) String nome,
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Hex inválido") String hex,
        Boolean ativo
) {}
