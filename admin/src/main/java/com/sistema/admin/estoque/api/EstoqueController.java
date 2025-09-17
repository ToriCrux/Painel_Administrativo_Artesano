package com.sistema.admin.estoque.api;

import com.sistema.admin.estoque.api.dto.EstoqueRequest;
import com.sistema.admin.estoque.api.dto.EstoqueResponse;
import com.sistema.admin.estoque.aplicacao.EstoqueService;
import com.sistema.admin.estoque.dominio.Estoque;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/estoque")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService estoqueService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<EstoqueResponse>> listarEstoques(
            @RequestParam(required = false) Long produtoId,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {

        Page<EstoqueResponse> page = estoqueService.listar(produtoId, pageable)
                .map(this::toResponse);

        if (page.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EstoqueResponse> buscarPorProduto(@PathVariable Long id) {
        var estoque = estoqueService.buscarPorProduto(id);
        return ResponseEntity.ok(toResponse(estoque));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstoqueResponse> ajustarSaldo(
            @PathVariable Long id,
            @RequestBody @Valid EstoqueRequest request) {
        var estoque = estoqueService.ajustarSaldo(id, request.getSaldo());
        return ResponseEntity.ok(toResponse(estoque));
    }

    private EstoqueResponse toResponse(Estoque estoque) {
        return EstoqueResponse.builder()
                .produtoId(estoque.getProdutoId())
                .saldo(estoque.getSaldo())
                .versao(estoque.getVersao())
                .atualizadoEm(estoque.getAtualizadoEm())
                .build();
    }
}
