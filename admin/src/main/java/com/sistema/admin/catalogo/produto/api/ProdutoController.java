package com.sistema.admin.catalogo.produto.api;

import com.sistema.admin.catalogo.produto.aplicacao.ProdutoService;

import com.sistema.admin.catalogo.produto.api.dto.ProdutoRequest;
import com.sistema.admin.catalogo.produto.api.dto.ProdutoResponse;
import com.sistema.admin.catalogo.produto.dominio.Produto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ProdutoResponse>> listarProdutos(
            @RequestParam(required = false) String nome,
            Pageable pageable) {

        Page<ProdutoResponse> page = produtoService.listar(nome, pageable);

        if (page.isEmpty()) return ResponseEntity.noContent().build();

        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProdutoResponse> listarProdutoPorId(@PathVariable Long id) {
        ProdutoResponse produto = produtoService.listarPorId(id);
        if (produto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(produto);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ProdutoResponse> salvarProduto(@RequestBody @Valid ProdutoRequest produtoRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.salvar(produtoRequest));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ProdutoResponse> atualizarProduto(@PathVariable Long id, @RequestBody @Valid ProdutoRequest produtoRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.atualizar(id, produtoRequest));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ProdutoResponse> desativarProduto(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.desativar(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public void deletar(@PathVariable Long id) {
        produtoService.deletar(id);
    }
}
