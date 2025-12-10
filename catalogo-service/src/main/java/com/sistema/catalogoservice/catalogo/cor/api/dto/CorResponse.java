package com.sistema.catalogoservice.catalogo.cor.api.dto;

import java.time.OffsetDateTime;

public record CorResponse(
        Long id,
        String nome,
        String hex,
        Boolean ativo,
        OffsetDateTime criadoEm,
        OffsetDateTime atualizadoEm
) {}
