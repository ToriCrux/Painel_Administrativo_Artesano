package com.sistema.catalogoservice.catalogo.produtocampoexibicao.api.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record ProdutoCampoExibicaoRequest(
        @NotNull Map<String, Boolean> campos
) {}
