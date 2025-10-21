package com.sistema.admin.catalogo.categoria.api;

import com.sistema.admin.catalogo.categoria.aplicacao.CategoriaService;

import com.sistema.admin.catalogo.categoria.api.dto.CategoriaRequest;
import com.sistema.admin.catalogo.categoria.api.dto.CategoriaResponse;
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

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CategoriaResponse> listarCategoriaPorId(@PathVariable("id") Long id) {
        CategoriaResponse categoria = categoriaService.listarPorId(id);
        if (categoria == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(categoria);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaResponse> salvarCategoria(@RequestBody @Valid CategoriaRequest categoriaRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.salvar(categoriaRequest));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaResponse> atualizarCategoria(@PathVariable Long id, @RequestBody @Valid CategoriaRequest categoriaRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.atualizar(id, categoriaRequest));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaResponse> desativar(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.desativar(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarCategoria(@PathVariable @Valid Long id) {
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
