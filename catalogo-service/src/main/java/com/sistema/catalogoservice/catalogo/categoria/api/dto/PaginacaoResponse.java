package com.sistema.catalogoservice.catalogo.categoria.api.dto;

import java.util.List;

public record PaginacaoResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {}
