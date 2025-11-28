package com.sistema.estoque_service.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstoqueTest {

    @Test
    @DisplayName("aumentar: deve somar quando quantidade positiva")
    void aumentar_ok() {
        Estoque e = Estoque.builder().produtoId(1L).saldo(10L).build();

        e.aumentar(5);

        assertThat(e.getSaldo()).isEqualTo(15L);
    }

    @Test
    @DisplayName("aumentar: deve lançar quando quantidade <= 0")
    void aumentar_invalido() {
        Estoque e = Estoque.builder().produtoId(1L).saldo(10L).build();

        assertThatThrownBy(() -> e.aumentar(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantidade deve ser positiva");

        assertThatThrownBy(() -> e.aumentar(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantidade deve ser positiva");
    }

    @Test
    @DisplayName("baixar: deve subtrair quando há saldo suficiente")
    void baixar_ok() {
        Estoque e = Estoque.builder().produtoId(1L).saldo(10L).build();

        e.baixar(4);

        assertThat(e.getSaldo()).isEqualTo(6L);
    }

    @Test
    @DisplayName("baixar: deve lançar quando saldo insuficiente")
    void baixar_saldoInsuficiente() {
        Estoque e = Estoque.builder().produtoId(1L).saldo(2L).build();

        assertThatThrownBy(() -> e.baixar(3))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Saldo insuficiente");
    }

    @Test
    @DisplayName("ajustar: deve lançar quando novoSaldo negativo")
    void ajustar_negativo() {
        Estoque e = Estoque.builder().produtoId(1L).saldo(2L).build();

        assertThatThrownBy(() -> e.ajustar(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Saldo não pode ser negativo");
    }

    @Test
    @DisplayName("prePersist: deve preencher criadoEm e atualizadoEm")
    void prePersist_preencheTimestamps() {
        Estoque e = Estoque.builder().produtoId(1L).saldo(0L).build();

        e.prePersist();

        assertThat(e.getCriadoEm()).isNotNull();
        assertThat(e.getAtualizadoEm()).isNotNull();
        assertThat(e.getAtualizadoEm()).isEqualTo(e.getCriadoEm());
    }

    @Test
    @DisplayName("preUpdate: deve atualizar atualizadoEm")
    void preUpdate_atualizaAtualizadoEm() {
        Estoque e = Estoque.builder().produtoId(1L).saldo(0L).criadoEm(OffsetDateTime.now().minusDays(1)).atualizadoEm(OffsetDateTime.now().minusDays(1)).build();
        OffsetDateTime before = e.getAtualizadoEm();

        e.preUpdate();

        assertThat(e.getAtualizadoEm()).isAfterOrEqualTo(before);
    }
}
