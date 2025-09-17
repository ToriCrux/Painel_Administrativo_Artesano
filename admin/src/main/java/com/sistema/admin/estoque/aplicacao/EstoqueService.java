package com.sistema.admin.estoque.aplicacao;

import com.sistema.admin.estoque.dominio.Estoque;
import com.sistema.admin.estoque.infra.EstoqueRepository;
import com.sistema.admin.catalogo.produto.dominio.Produto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository repository;

    public Page<Estoque> listar(Long produtoId, Pageable pageable) {
        return (produtoId != null)
                ? repository.findByProdutoId(produtoId, pageable)
                : repository.findAll(pageable);
    }

    public Estoque buscarPorProduto(Long produtoId) {
        return repository.findByProdutoId(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Estoque não encontrado para produto " + produtoId));
    }

    public Estoque ajustarSaldo(Long produtoId, Long novoSaldo) {
        var estoque = buscarPorProduto(produtoId);
        estoque.ajustar(novoSaldo); // método de domínio do agregado
        return repository.save(estoque);
    }

    // Se quiser comandos explícitos:
    public Estoque aumentar(Long produtoId, Long qtd) {
        var e = buscarPorProduto(produtoId);
        e.aumentar(qtd);
        return repository.save(e);
    }

    public Estoque baixar(Long produtoId, Long qtd) {
        var e = buscarPorProduto(produtoId);
        e.baixar(qtd);
        return repository.save(e);
    }

    public Estoque criarEstoqueParaProduto(Long produtoId) {
        var e = Estoque.builder()
                .produtoId(produtoId)
                .saldo(0L)
                .build();
        return repository.save(e);
    }

    public void deletarPorProduto(Long produtoId) {
        repository.findByProdutoId(produtoId).ifPresent(repository::delete);
    }
}
