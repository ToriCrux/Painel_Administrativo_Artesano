package com.sistema.catalogoservice.catalogo.produtocampoexibicao.api;

import com.sistema.catalogoservice.catalogo.produtocampoexibicao.api.dto.ProdutoCampoExibicaoRequest;
import com.sistema.catalogoservice.catalogo.produtocampoexibicao.api.dto.ProdutoCampoExibicaoResponse;
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

    @GetMapping("/exibicao")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProdutoCampoExibicaoResponse>> listarConfiguracoes() {
        return ResponseEntity.ok(service.listarConfiguracoes());
    }

    @PutMapping("/{produtoId}/exibicao")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProdutoCampoExibicaoResponse> atualizarConfiguracoes(
            @PathVariable Long produtoId,
            @RequestBody @Valid ProdutoCampoExibicaoRequest request
    ) {
        return ResponseEntity.ok(service.atualizarConfiguracoes(produtoId, request));
    }
}
