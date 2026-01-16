package com.sistema.catalogoservice.catalogo.produto.api.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record CorGrupoRequest(
        @NotBlank String corNome,
        List<String> subcorNomes
) {}
