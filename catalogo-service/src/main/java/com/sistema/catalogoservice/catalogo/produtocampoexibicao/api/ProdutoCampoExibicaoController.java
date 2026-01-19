package com.sistema.catalogoservice.catalogo.produtocampoexibicao.api;

import com.sistema.catalogoservice.catalogo.produtocampoexibicao.api.dto.*;
import com.sistema.catalogoservice.catalogo.produtocampoexibicao.aplicacao.ProdutoCampoExibicaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/produtos")
@RequiredArgsConstructor
public class ProdutoCampoExibicaoController {

    private final ProdutoCampoExibicaoService service;

    // Lista configs (default + overrides) para todos os produtos ativos
    @GetMapping("/exibicao")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProdutoCampoExibicaoResponse>> listarConfiguracoes() {
        return ResponseEntity.ok(service.listarConfiguracoes());
    }

    // Busca 1 produto com config + exclusoes
    @GetMapping("/{produtoId}/exibicao")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProdutoExibicaoDetalhadaResponse> buscarExibicaoProduto(@PathVariable Long produtoId) {
        return ResponseEntity.ok(service.buscarExibicaoProduto(produtoId));
    }

    // ✅ Atualiza campos booleanos
    // IMPORTANTE: este é o endpoint que o front precisa chamar
    @PutMapping("/{produtoId}/exibicao/config")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProdutoCampoExibicaoResponse> atualizarConfiguracoes(
            @PathVariable Long produtoId,
            @RequestBody @Valid ProdutoCampoExibicaoRequest request
    ) {
        return ResponseEntity.ok(service.atualizarConfiguracoes(produtoId, request));
    }

    // Atualiza exclusões (substitui o conjunto inteiro)
    @PutMapping("/{produtoId}/exibicao/exclusoes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProdutoExibicaoExclusoesResponse> atualizarExclusoes(
            @PathVariable Long produtoId,
            @RequestBody @Valid ProdutoExibicaoExclusoesRequest request
    ) {
        return ResponseEntity.ok(service.atualizarExclusoes(produtoId, request));
    }
}
