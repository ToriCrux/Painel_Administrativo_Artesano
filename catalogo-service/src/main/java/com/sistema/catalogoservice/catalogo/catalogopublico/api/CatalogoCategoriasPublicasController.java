package com.sistema.catalogoservice.catalogo.catalogopublico.api;

import com.sistema.catalogoservice.catalogo.catalogopublico.aplicacao.CatalogoCategoriaPublicaService;
import com.sistema.catalogoservice.catalogo.catalogopublico.dto.CategoriaFiltroPublicoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/catalogo")
@RequiredArgsConstructor
public class CatalogoCategoriasPublicasController {

    private final CatalogoCategoriaPublicaService service;

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaFiltroPublicoResponse>> listarCategoriasAtivas() {
        var result = service.listarCategoriasAtivasParaFiltro();
        if (result.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(result);
    }
}
