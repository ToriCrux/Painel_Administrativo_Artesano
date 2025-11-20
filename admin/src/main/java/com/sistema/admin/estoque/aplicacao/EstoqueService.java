package com.sistema.admin.estoque.aplicacao;

import com.sistema.admin.estoque.dominio.Estoque;
import com.sistema.admin.estoque.dominio.MovimentacaoEstoque;
import com.sistema.admin.estoque.infra.EstoqueRepository;
import com.sistema.admin.estoque.infra.MovimentacaoEstoqueRepository;
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
        Long saldoAnterior = estoque.getSaldo();

        estoque.ajustar(novoSaldo);
        var salvo = repository.save(estoque);

        registrarMovimentacao(produtoId, "AJUSTE", novoSaldo - saldoAnterior, saldoAnterior, salvo.getSaldo());

        return salvo;
    }

    public Estoque aumentar(Long produtoId, Long qtd) {
        var e = buscarPorProduto(produtoId);
        Long saldoAnterior = e.getSaldo();

        e.aumentar(qtd);
        var salvo = repository.save(e);

        registrarMovimentacao(produtoId, "ENTRADA", qtd, saldoAnterior, salvo.getSaldo());

        return salvo;
    }

    public Estoque baixar(Long produtoId, Long qtd) {
        var e = buscarPorProduto(produtoId);
        Long saldoAnterior = e.getSaldo();

        e.baixar(qtd);
        var salvo = repository.save(e);

        registrarMovimentacao(produtoId, "SAIDA", qtd, saldoAnterior, salvo.getSaldo());

        return salvo;
    }

    public Estoque criarEstoqueParaProduto(Long produtoId) {
        var e = Estoque.builder()
                .produtoId(produtoId)
                .saldo(0L)
                .build();

        var salvo = repository.save(e);

        registrarMovimentacao(produtoId, "CRIACAO", 0L, 0L, salvo.getSaldo());

        return salvo;
    }

    public void deletarPorProduto(Long produtoId) {
        repository.findByProdutoId(produtoId).ifPresent(repository::delete);
    }

    public List<MovimentacaoEstoque> listarMovimentacoes(Long produtoId) {
        return movRepository.findByProdutoIdOrderByCriadoEmDesc(produtoId);
    }

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
}
