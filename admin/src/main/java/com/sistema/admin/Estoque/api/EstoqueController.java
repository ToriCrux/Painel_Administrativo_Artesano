package com.sistema.admin.Estoque.api;

import com.sistema.admin.Estoque.api.dto.EstoqueRequest;
import com.sistema.admin.Estoque.api.dto.EstoqueResponse;
import com.sistema.admin.Estoque.aplicacao.EstoqueService;
import com.sistema.admin.Estoque.dominio.Estoque;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/estoque")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService service;

    /**
     * Lista todos os estoques.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<EstoqueResponse> listar() {
        return service.listarTodos()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Consulta o estoque de um produto pelo ID do produto.
     */
    @GetMapping("/{produtoId}")
    @PreAuthorize("isAuthenticated()")
    public EstoqueResponse buscarPorProduto(@PathVariable Long produtoId) {
        Estoque estoque = service.buscarPorProduto(produtoId);
        return toResponse(estoque);
    }

    /**
     * Atualiza a quantidade de estoque de um produto.
     */
    @PutMapping("/{produtoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public EstoqueResponse atualizarQuantidade(@PathVariable Long produtoId,
                                               @RequestBody @Valid EstoqueRequest request) {
        Estoque estoque = service.atualizarQuantidade(produtoId, request.getQuantidadeAtual());
        return toResponse(estoque);
    }

    /**
     * Converte entidade para DTO de resposta.
     */
    private EstoqueResponse toResponse(Estoque estoque) {
        return EstoqueResponse.builder()
                .produtoId(estoque.getProduto().getId())
                .nomeProduto(estoque.getProduto().getNome())
                .quantidadeAtual(estoque.getQuantidadeAtual())
                .build();
    }
}
