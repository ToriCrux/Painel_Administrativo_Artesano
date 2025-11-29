package com.sistema.estoque_service.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MovimentacaoEstoqueTest {

    @Test
    @DisplayName("prePersist: deve preencher criadoEm")
    void prePersist_preencheCriadoEm() {
        MovimentacaoEstoque mov = MovimentacaoEstoque.builder()
                .produtoId(1L)
                .tipo("ENTRADA")
                .quantidade(1L)
                .saldoAnterior(0L)
                .saldoNovo(1L)
                .build();

        mov.prePersist();

        assertThat(mov.getCriadoEm()).isNotNull();
    }
}
