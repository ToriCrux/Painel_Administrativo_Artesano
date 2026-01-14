package com.sistema.catalogoservice.catalogo.categoria.api;

import com.sistema.catalogoservice.catalogo.categoria.api.dto.CategoriaRequest;
import com.sistema.catalogoservice.catalogo.categoria.api.dto.CategoriaResponse;
import com.sistema.catalogoservice.catalogo.categoria.api.dto.PaginacaoResponse;
import com.sistema.catalogoservice.catalogo.categoria.aplicacao.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaResponse> criar(@RequestBody @Valid CategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.salvar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> atualizar(@PathVariable Long id, @RequestBody @Valid CategoriaRequest request) {
        return ResponseEntity.ok(categoriaService.atualizar(id, request));
    }

    // ✅ NOVO GET PAGINADO COM DTO CUSTOMIZADO
    @GetMapping
    public ResponseEntity<PaginacaoResponse<CategoriaResponse>> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return ResponseEntity.ok(categoriaService.listarPaginado(page, size, sort, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.listarPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
