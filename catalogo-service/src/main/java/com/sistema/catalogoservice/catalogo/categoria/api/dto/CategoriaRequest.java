package com.sistema.catalogoservice.catalogo.categoria.api.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record CategoriaRequest(
        @NotBlank String nome,
        boolean ativo,
        List<SubcategoriaRequest> subcategorias
) {}
