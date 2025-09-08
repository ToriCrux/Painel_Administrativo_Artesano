package com.sistema.admin.controle.dto.cor;

import java.time.LocalDateTime;

public record CorResponseDTO(
        Long id,
        String nome,
        String hex,
        Boolean ativo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {}
