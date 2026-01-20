package com.sistema.catalogoservice.catalogo.catalogopublico.api;

import com.sistema.catalogoservice.catalogo.catalogopublico.aplicacao.CatalogoCorPublicaService;
import com.sistema.catalogoservice.catalogo.catalogopublico.dto.CorFiltroPublicoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/catalogo")
@RequiredArgsConstructor
public class CatalogoCoresPublicasController {

    private final CatalogoCorPublicaService service;

    @GetMapping("/cores")
    public ResponseEntity<List<CorFiltroPublicoResponse>> listarCoresAtivas() {
        var result = service.listarCoresAtivasParaFiltro();
        if (result.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(result);
    }
}
