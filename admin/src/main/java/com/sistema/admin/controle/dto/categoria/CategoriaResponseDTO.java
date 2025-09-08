package com.sistema.admin.controle.dto.categoria;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record CategoriaResponseDTO(
        Long id,
        String nome,
        Boolean ativo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {}

