package com.sistema.catalogoservice.catalogo.categoria.api.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record SubcategoriaRequest(
        @NotBlank String nome,
        boolean ativo,
        List<SubcategoriaRequest> subcategorias // ✅ recursivo: permite níveis infinitos
) {}
