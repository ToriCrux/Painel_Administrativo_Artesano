package com.sistema.catalogoservice.catalogo.produto.api.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record CategoriaHierarquiaRequest(
        @NotBlank String categoriaNome,
        List<SubcategoriaHierarquiaRequest> subcategorias
) {}
