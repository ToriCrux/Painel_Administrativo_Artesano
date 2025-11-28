package com.sistema.estoque_service.api;

import com.sistema.estoque_service.api.dto.EstoqueRequest;
import com.sistema.estoque_service.api.dto.EstoqueResponse;
import com.sistema.estoque_service.aplicacao.EstoqueService;
import com.sistema.estoque_service.dominio.Estoque;
import com.sistema.estoque_service.dominio.MovimentacaoEstoque;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstoqueControllerTest {

    @Mock
    EstoqueService estoqueService;

    @InjectMocks
    EstoqueController controller;

    @Test
    @DisplayName("listarEstoques: 200 quando há itens")
    void listarEstoques_ok() {
        var pageable = PageRequest.of(0, 10);
        var now = OffsetDateTime.parse("2025-09-25T18:30:00-03:00");

        var e1 = Estoque.builder().id(1L).produtoId(10L).saldo(5L).versao(1L).atualizadoEm(now).build();
        var e2 = Estoque.builder().id(2L).produtoId(20L).saldo(8L).versao(2L).atualizadoEm(now).build();

        when(estoqueService.listar(isNull(), any())).thenReturn(new PageImpl<>(List.of(e1, e2), pageable, 2));

        ResponseEntity<Page<EstoqueResponse>> resp = controller.listarEstoques(null, pageable);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getContent()).hasSize(2);
        assertThat(resp.getBody().getContent()).extracting(EstoqueResponse::getProdutoId).containsExactly(10L, 20L);

        verify(estoqueService).listar(isNull(), eq(pageable));
    }

    @Test
    @DisplayName("listarEstoques: 204 quando página vazia")
    void listarEstoques_noContent() {
        var pageable = PageRequest.of(0, 10);
        when(estoqueService.listar(any(), any()))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        ResponseEntity<Page<EstoqueResponse>> resp = controller.listarEstoques(null, pageable);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(resp.getBody()).isNull();
    }

    @Test
    @DisplayName("buscarPorProduto: 200 e body")
    void buscarPorProduto_ok() {
        var now = OffsetDateTime.parse("2025-09-25T18:30:00-03:00");
        var e = Estoque.builder().id(1L).produtoId(10L).saldo(5L).versao(3L).atualizadoEm(now).build();
        when(estoqueService.buscarPorProduto(10L)).thenReturn(e);

        ResponseEntity<EstoqueResponse> resp = controller.buscarPorProduto(10L);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getProdutoId()).isEqualTo(10L);
        assertThat(resp.getBody().getSaldo()).isEqualTo(5L);
        assertThat(resp.getBody().getVersao()).isEqualTo(3L);
        assertThat(resp.getBody().getAtualizadoEm()).isEqualTo(now);
    }

    @Test
    @DisplayName("ajustarSaldo: 200 e chama service com saldo do request")
    void ajustarSaldo_ok() {
        var req = new EstoqueRequest();
        req.setSaldo(12L);

        when(estoqueService.ajustarSaldo(10L, 12L))
                .thenReturn(Estoque.builder().id(1L).produtoId(10L).saldo(12L).versao(1L).atualizadoEm(OffsetDateTime.now()).build());

        ResponseEntity<EstoqueResponse> resp = controller.ajustarSaldo(10L, req);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        verify(estoqueService).ajustarSaldo(10L, 12L);
    }

    @Test
    @DisplayName("entrada: 200 e chama service")
    void entrada_ok() {
        var req = new EstoqueRequest();
        req.setSaldo(3L);

        when(estoqueService.aumentar(10L, 3L))
                .thenReturn(Estoque.builder().id(1L).produtoId(10L).saldo(8L).versao(1L).atualizadoEm(OffsetDateTime.now()).build());

        ResponseEntity<EstoqueResponse> resp = controller.entrada(10L, req);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(estoqueService).aumentar(10L, 3L);
    }

    @Test
    @DisplayName("saida: 200 e chama service")
    void saida_ok() {
        var req = new EstoqueRequest();
        req.setSaldo(4L);

        when(estoqueService.baixar(10L, 4L))
                .thenReturn(Estoque.builder().id(1L).produtoId(10L).saldo(6L).versao(1L).atualizadoEm(OffsetDateTime.now()).build());

        ResponseEntity<EstoqueResponse> resp = controller.saida(10L, req);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(estoqueService).baixar(10L, 4L);
    }

    @Test
    @DisplayName("listarMovimentacoes: 200 quando há itens")
    void listarMovimentacoes_ok() {
        var movs = List.of(
                MovimentacaoEstoque.builder().id(1L).produtoId(10L).tipo("ENTRADA").quantidade(1L).saldoAnterior(0L).saldoNovo(1L).build()
        );
        when(estoqueService.listarMovimentacoes(10L)).thenReturn(movs);

        ResponseEntity<List<MovimentacaoEstoque>> resp = controller.listarMovimentacoes(10L);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("listarMovimentacoes: 204 quando vazio")
    void listarMovimentacoes_noContent() {
        when(estoqueService.listarMovimentacoes(10L)).thenReturn(List.of());

        ResponseEntity<List<MovimentacaoEstoque>> resp = controller.listarMovimentacoes(10L);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(resp.getBody()).isNull();
    }
}
