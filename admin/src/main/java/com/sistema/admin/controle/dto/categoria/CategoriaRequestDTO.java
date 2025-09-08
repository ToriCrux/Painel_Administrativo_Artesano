package com.sistema.admin.controle.dto.categoria;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequestDTO(
        @NotBlank String nome,
        boolean ativo
) {}

