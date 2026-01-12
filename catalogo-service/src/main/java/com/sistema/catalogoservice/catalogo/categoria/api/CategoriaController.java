package com.sistema.catalogoservice.catalogo.categoria.api;

import com.sistema.catalogoservice.catalogo.categoria.api.dto.CategoriaRequest;
import com.sistema.catalogoservice.catalogo.categoria.api.dto.CategoriaResponse;
import com.sistema.catalogoservice.catalogo.categoria.aplicacao.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<CategoriaResponse>> listarCategorias(
            @RequestParam(name = "nome", required = false) String nome,
            @ParameterObject Pageable pageable) {

        Page<CategoriaResponse> page = categoriaService.listar(nome, pageable);
        if (page.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(page);
    }

    // ✅ opcional: listar todas (inclui subcategorias isoladas)
    @GetMapping("/todas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<CategoriaResponse>> listarTodas(@ParameterObject Pageable pageable) {
        Page<CategoriaResponse> page = categoriaService.listarTodas(pageable);
        if (page.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CategoriaResponse> listarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.listarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CategoriaResponse> salvar(@RequestBody @Valid CategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.salvar(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CategoriaResponse> atualizar(@PathVariable Long id, @RequestBody @Valid CategoriaRequest request) {
        return ResponseEntity.ok(categoriaService.atualizar(id, request));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CategoriaResponse> desativar(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.desativar(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
