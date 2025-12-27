package com.sistema.catalogoservice.catalogo.catalogopublico.api;

import com.sistema.catalogoservice.catalogo.catalogopublico.aplicacao.CatalogoPublicoService;
import com.sistema.catalogoservice.catalogo.catalogopublico.dto.CatalogoProdutoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/catalogo")
@RequiredArgsConstructor
public class CatalogoPublicoController {

    private final CatalogoPublicoService catalogoPublicoService;

    @GetMapping
    public ResponseEntity<List<CatalogoProdutoResponse>> listarCatalogoPublico() {
        var produtos = catalogoPublicoService.listarProdutosPublicos();
        if (produtos.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(produtos);
    }
}
