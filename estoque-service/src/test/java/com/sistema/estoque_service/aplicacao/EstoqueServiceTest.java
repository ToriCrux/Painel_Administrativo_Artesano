package com.sistema.estoque_service.aplicacao;

import com.sistema.estoque_service.dominio.Estoque;
import com.sistema.estoque_service.dominio.MovimentacaoEstoque;
import com.sistema.estoque_service.infra.EstoqueRepository;
import com.sistema.estoque_service.infra.MovimentacaoEstoqueRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    @Mock
    EstoqueRepository repository;

    @Mock
    MovimentacaoEstoqueRepository movRepository;

    @InjectMocks
    EstoqueService service;

    private static Estoque estoque(Long id, Long produtoId, long saldo) {
        return Estoque.builder()
                .id(id)
                .produtoId(produtoId)
                .saldo(saldo)
                .build();
    }

    @Test
    @DisplayName("listar: quando produtoId é null deve usar findAll")
    void listar_quandoProdutoIdNull_deveUsarFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(estoque(1L, 10L, 5L)), pageable, 1));

        var page = service.listar(null, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        verify(repository).findAll(pageable);
        verify(repository, never()).findByProdutoId(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("listar: quando produtoId informado deve usar findByProdutoId")
    void listar_quandoProdutoIdInformado_deveUsarFindByProdutoId() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findByProdutoId(eq(10L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(estoque(1L, 10L, 5L)), pageable, 1));

        var page = service.listar(10L, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        verify(repository).findByProdutoId(10L, pageable);
        verify(repository, never()).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("buscarPorProduto: deve retornar estoque quando existir")
    void buscarPorProduto_ok() {
        when(repository.findByProdutoId(10L)).thenReturn(Optional.of(estoque(1L, 10L, 7L)));

        Estoque e = service.buscarPorProduto(10L);

        assertThat(e.getProdutoId()).isEqualTo(10L);
        assertThat(e.getSaldo()).isEqualTo(7L);
    }

    @Test
    @DisplayName("buscarPorProduto: deve lançar EntityNotFoundException quando não existir")
    void buscarPorProduto_quandoNaoExiste_deveLancar() {
        when(repository.findByProdutoId(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorProduto(10L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Estoque não encontrado para produto 10");
    }

    @Test
    @DisplayName("ajustarSaldo: deve alterar saldo e registrar movimentação AJUSTE")
    void ajustarSaldo_ok_deveSalvarERegistrarMov() {
        var existente = estoque(1L, 10L, 5L);
        when(repository.findByProdutoId(10L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Estoque.class))).thenAnswer(inv -> inv.getArgument(0));
        when(movRepository.save(any(MovimentacaoEstoque.class))).thenAnswer(inv -> inv.getArgument(0));

        Estoque salvo = service.ajustarSaldo(10L, 12L);

        assertThat(salvo.getSaldo()).isEqualTo(12L);

        ArgumentCaptor<MovimentacaoEstoque> movCaptor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);
        verify(movRepository).save(movCaptor.capture());
        MovimentacaoEstoque mov = movCaptor.getValue();

        assertThat(mov.getProdutoId()).isEqualTo(10L);
        assertThat(mov.getTipo()).isEqualTo("AJUSTE");
        assertThat(mov.getQuantidade()).isEqualTo(12L - 5L);
        assertThat(mov.getSaldoAnterior()).isEqualTo(5L);
        assertThat(mov.getSaldoNovo()).isEqualTo(12L);
    }

    @Test
    @DisplayName("aumentar: deve incrementar saldo e registrar movimentação ENTRADA")
    void aumentar_ok_deveSalvarERegistrarMov() {
        var existente = estoque(1L, 10L, 5L);
        when(repository.findByProdutoId(10L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Estoque.class))).thenAnswer(inv -> inv.getArgument(0));

        service.aumentar(10L, 3L);

        assertThat(existente.getSaldo()).isEqualTo(8L);

        ArgumentCaptor<MovimentacaoEstoque> movCaptor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);
        verify(movRepository).save(movCaptor.capture());
        assertThat(movCaptor.getValue().getTipo()).isEqualTo("ENTRADA");
        assertThat(movCaptor.getValue().getQuantidade()).isEqualTo(3L);
        assertThat(movCaptor.getValue().getSaldoAnterior()).isEqualTo(5L);
        assertThat(movCaptor.getValue().getSaldoNovo()).isEqualTo(8L);
    }

    @Test
    @DisplayName("baixar: deve decrementar saldo e registrar movimentação SAIDA")
    void baixar_ok_deveSalvarERegistrarMov() {
        var existente = estoque(1L, 10L, 10L);
        when(repository.findByProdutoId(10L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Estoque.class))).thenAnswer(inv -> inv.getArgument(0));

        service.baixar(10L, 4L);

        assertThat(existente.getSaldo()).isEqualTo(6L);

        ArgumentCaptor<MovimentacaoEstoque> movCaptor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);
        verify(movRepository).save(movCaptor.capture());
        assertThat(movCaptor.getValue().getTipo()).isEqualTo("SAIDA");
        assertThat(movCaptor.getValue().getQuantidade()).isEqualTo(4L);
        assertThat(movCaptor.getValue().getSaldoAnterior()).isEqualTo(10L);
        assertThat(movCaptor.getValue().getSaldoNovo()).isEqualTo(6L);
    }

    @Test
    @DisplayName("baixar: deve lançar IllegalStateException quando saldo insuficiente")
    void baixar_quandoSaldoInsuficiente_deveLancar() {
        var existente = estoque(1L, 10L, 2L);
        when(repository.findByProdutoId(10L)).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> service.baixar(10L, 3L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Saldo insuficiente");
        verify(repository, never()).save(any());
        verify(movRepository, never()).save(any());
    }

    @Test
    @DisplayName("criarEstoqueParaProduto: deve criar com saldo 0 e registrar movimentação CRIACAO")
    void criarEstoqueParaProduto_ok() {
        when(repository.save(any(Estoque.class))).thenAnswer(inv -> {
            Estoque e = inv.getArgument(0);
            // simula id gerado para não ficar null em logs/testes
            return e.toBuilder().id(1L).build();
        });

        service.criarEstoqueParaProduto(99L);

        ArgumentCaptor<Estoque> estoqueCaptor = ArgumentCaptor.forClass(Estoque.class);
        verify(repository).save(estoqueCaptor.capture());
        assertThat(estoqueCaptor.getValue().getProdutoId()).isEqualTo(99L);
        assertThat(estoqueCaptor.getValue().getSaldo()).isEqualTo(0L);

        ArgumentCaptor<MovimentacaoEstoque> movCaptor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);
        verify(movRepository).save(movCaptor.capture());
        assertThat(movCaptor.getValue().getTipo()).isEqualTo("CRIACAO");
        assertThat(movCaptor.getValue().getQuantidade()).isEqualTo(0L);
        assertThat(movCaptor.getValue().getSaldoAnterior()).isEqualTo(0L);
        assertThat(movCaptor.getValue().getSaldoNovo()).isEqualTo(0L);
    }

    @Test
    @DisplayName("deletarPorProduto: quando existir deve deletar")
    void deletarPorProduto_quandoExiste() {
        var existente = estoque(1L, 10L, 0L);
        when(repository.findByProdutoId(10L)).thenReturn(Optional.of(existente));

        service.deletarPorProduto(10L);

        verify(repository).delete(existente);
    }

    @Test
    @DisplayName("deletarPorProduto: quando não existir não deve deletar")
    void deletarPorProduto_quandoNaoExiste() {
        when(repository.findByProdutoId(10L)).thenReturn(Optional.empty());

        service.deletarPorProduto(10L);

        verify(repository, never()).delete(any());
    }

    @Test
    @DisplayName("listarMovimentacoes: deve delegar para repository")
    void listarMovimentacoes_ok() {
        when(movRepository.findByProdutoIdOrderByCriadoEmDesc(10L))
                .thenReturn(List.of(MovimentacaoEstoque.builder().id(1L).produtoId(10L).tipo("ENTRADA").quantidade(1L).saldoAnterior(0L).saldoNovo(1L).build()));

        var list = service.listarMovimentacoes(10L);

        assertThat(list).hasSize(1);
        verify(movRepository).findByProdutoIdOrderByCriadoEmDesc(10L);
    }
}
