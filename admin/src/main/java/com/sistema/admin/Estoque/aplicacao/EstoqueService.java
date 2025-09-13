package com.sistema.admin.Estoque.aplicacao;

import com.sistema.admin.Estoque.dominio.Estoque;
import com.sistema.admin.Estoque.infra.EstoqueRepository;
import com.sistema.admin.catalogo.produto.dominio.Produto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository repository;

    /**
     * Lista todos os estoques cadastrados.
     */
    public List<Estoque> listarTodos() {
        return repository.findAll();
    }

    /**
     * Busca o estoque pelo ID do produto.
     */
    public Estoque buscarPorProduto(Long produtoId) {
        return repository.findByProdutoId(produtoId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Estoque não encontrado para o produto ID: " + produtoId
                ));
    }

    /**
     * Atualiza a quantidade de um produto em estoque.
     */
    public Estoque atualizarQuantidade(Long produtoId, Integer novaQuantidade) {
        Estoque estoque = buscarPorProduto(produtoId);
        estoque.setQuantidadeAtual(novaQuantidade);
        return repository.save(estoque);
    }

    /**
     * Cria estoque inicial para um novo produto.
     */
    public Estoque criarEstoqueParaProduto(Produto produto) {
        Estoque estoque = Estoque.builder()
                .produto(produto)
                .quantidadeAtual(0)
                .build();

        return repository.save(estoque);
    }

    /**
     * Remove o estoque de um produto (ex.: quando produto é deletado).
     */
    public void deletarPorProduto(Long produtoId) {
        Estoque estoque = buscarPorProduto(produtoId);
        repository.delete(estoque);
    }
}
