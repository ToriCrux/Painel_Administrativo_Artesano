package com.sistema.admin.controle.dto.cor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CorRequestDTO(
        @NotBlank @Size(max = 60) String nome,
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Hex inválido") String hex,
        Boolean ativo
) {}
