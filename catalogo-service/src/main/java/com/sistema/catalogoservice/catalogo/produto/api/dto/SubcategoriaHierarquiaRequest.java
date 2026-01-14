package com.sistema.catalogoservice.catalogo.produto.api.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record SubcategoriaHierarquiaRequest(
        @NotBlank String subcategoriaNome,
        List<String> itens
) {}
