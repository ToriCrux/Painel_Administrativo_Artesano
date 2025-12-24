package com.sistema.estoque_service.aplicacao;

import com.sistema.estoque_service.dominio.Estoque;
import com.sistema.estoque_service.dominio.MovimentacaoEstoque;
import com.sistema.estoque_service.infra.EstoqueRepository;
import com.sistema.estoque_service.infra.MovimentacaoEstoqueRepository;
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
    private final MovimentacaoEstoqueRepository movRepository;

    // ========================
    // 🔹 Listagem
    // ========================
    public Page<Estoque> listar(Long produtoId, Pageable pageable) {
        return (produtoId != null)
                ? repository.findByProdutoId(produtoId, pageable)
                : repository.findAll(pageable);
    }

    // ========================
    // 🔹 Buscar estoque de produto
    // ========================
    public Estoque buscarPorProduto(Long produtoId) {
        return repository.findByProdutoId(produtoId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Estoque não encontrado para produto " + produtoId));
    }

    // ========================
    // 🔹 Ajustar saldo
    // ========================
    public Estoque ajustarSaldo(Long produtoId, Long novoSaldo) {
        var estoque = buscarPorProduto(produtoId);
        Long saldoAnterior = estoque.getSaldo();

        estoque.ajustar(novoSaldo);
        var salvo = repository.save(estoque);

        registrarMovimentacao(produtoId, "AJUSTE",
                novoSaldo - saldoAnterior, saldoAnterior, salvo.getSaldo());

        return salvo;
    }

    // ========================
    // 🔹 Entrada
    // ========================
    public Estoque aumentar(Long produtoId, Long qtd) {
        var e = buscarPorProduto(produtoId);
        Long saldoAnterior = e.getSaldo();

        e.aumentar(qtd);
        var salvo = repository.save(e);

        registrarMovimentacao(produtoId, "ENTRADA", qtd, saldoAnterior, salvo.getSaldo());
        return salvo;
    }

    // ========================
    // 🔹 Saída
    // ========================
    public Estoque baixar(Long produtoId, Long qtd) {
        var e = buscarPorProduto(produtoId);
        Long saldoAnterior = e.getSaldo();

        e.baixar(qtd);
        var salvo = repository.save(e);

        registrarMovimentacao(produtoId, "SAIDA", qtd, saldoAnterior, salvo.getSaldo());
        return salvo;
    }

    // ========================
    // 🔹 Criar estoque (com nome/código)
    // ========================
    public Estoque criarEstoqueParaProduto(Long produtoId, String produtoNome, String produtoCodigo) {
        var e = Estoque.builder()
                .produtoId(produtoId)
                .produtoNome(produtoNome != null ? produtoNome : "(Produto removido)")
                .produtoCodigo(produtoCodigo != null ? produtoCodigo : "—")
                .saldo(0L)
                .ativo(true)
                .build();

        var salvo = repository.save(e);
        registrarMovimentacao(produtoId, "CRIACAO", 0L, 0L, salvo.getSaldo());
        return salvo;
    }

    // ========================
    // 🔹 Criar estoque (retrocompatível)
    // ========================
    public Estoque criarEstoqueParaProduto(Long produtoId) {
        return criarEstoqueParaProduto(produtoId, "Desconhecido", "—");
    }

    // ========================
    // 🔹 Criar estoque zerado se não existir
    // ========================
    public Estoque criarEstoqueZeradoSeNaoExistir(Long produtoId) {
        var existente = repository.findByProdutoId(produtoId);
        if (existente.isPresent()) {
            throw new IllegalStateException("Estoque já existe para este produto.");
        }

        var novo = Estoque.builder()
                .produtoId(produtoId)
                .produtoCodigo("—")
                .produtoNome("(Produto removido)")
                .saldo(0L)
                .ativo(true)
                .build();

        var salvo = repository.save(novo);
        registrarMovimentacao(produtoId, "CRIACAO", 0L, 0L, salvo.getSaldo());
        return salvo;
    }

    // ========================
    // 🔹 Marcar produto como excluído
    // ========================
    public void marcarProdutoComoExcluido(Long produtoId) {
        repository.findByProdutoId(produtoId).ifPresent(estoque -> {
            estoque.marcarComoExcluido();
            repository.save(estoque);
        });
    }

    // ========================
    // 🔹 Deletar estoque por produto
    // ========================
    public void deletarPorProduto(Long produtoId) {
        repository.findByProdutoId(produtoId).ifPresent(repository::delete);
    }

    // ========================
    // 🔹 Listar movimentações
    // ========================
    public List<MovimentacaoEstoque> listarMovimentacoes(Long produtoId) {
        return movRepository.findByProdutoIdOrderByCriadoEmDesc(produtoId);
    }

    // ========================
    // 🔹 Registrar movimentação
    // ========================
    private void registrarMovimentacao(Long produtoId, String tipo, Long quantidade,
                                       Long saldoAnterior, Long saldoNovo) {
        MovimentacaoEstoque mov = MovimentacaoEstoque.builder()
                .produtoId(produtoId)
                .tipo(tipo)
                .quantidade(quantidade)
                .saldoAnterior(saldoAnterior)
                .saldoNovo(saldoNovo)
                .build();

        movRepository.save(mov);
    }

    // ========================
// 🔹 Registrar saída por pedido (tipo CLIENTE)
// ========================
    public void registrarSaidaPorPedido(Long produtoId, Long qtd, String codigoPedido, String nomeCliente) {
        var e = buscarPorProduto(produtoId);
        Long saldoAnterior = e.getSaldo();

        e.baixar(qtd);
        var salvo = repository.save(e);

        String descricao = String.format("Baixa automática — Pedido %s (Cliente: %s)", codigoPedido, nomeCliente);

        registrarMovimentacaoComDescricao(produtoId, "CLIENTE", qtd, saldoAnterior, salvo.getSaldo(), descricao);
    }

    private void registrarMovimentacaoComDescricao(Long produtoId, String tipo, Long quantidade,
                                                   Long saldoAnterior, Long saldoNovo, String descricao) {
        MovimentacaoEstoque mov = MovimentacaoEstoque.builder()
                .produtoId(produtoId)
                .tipo(tipo)
                .quantidade(quantidade)
                .saldoAnterior(saldoAnterior)
                .saldoNovo(saldoNovo)
                .descricao(descricao)
                .build();

        movRepository.save(mov);
    }
}
