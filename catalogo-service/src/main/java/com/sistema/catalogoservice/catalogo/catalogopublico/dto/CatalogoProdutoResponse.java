package com.sistema.catalogoservice.catalogo.catalogopublico.dto;

import java.util.List;
import java.util.Map;

public record CatalogoProdutoResponse(
        Long id,
        String codigo,
        Map<String, Object> camposVisiveis,
        ImagemPrincipal imagemPrincipal,
        List<ImagemSecundaria> imagens
) {
    public record ImagemPrincipal(Long id, String url, String contentType, String nomeArquivo) {}
    public record ImagemSecundaria(Long id, String url, String contentType, String nomeArquivo, Boolean principal, Integer ordem) {}
}
