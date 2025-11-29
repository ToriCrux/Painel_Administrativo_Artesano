package com.sistema.admin.catalogo.cor.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CorTest {

    @Test
    @DisplayName("@PrePersist deve preencher criadoEm")
    void prePersistShouldSetCreatedAt() {
        Cor cor = new Cor();
        assertThat(cor.getCriadoEm()).isNull();

        cor.prePersist();

        assertThat(cor.getCriadoEm()).isNotNull();
    }

    @Test
    @DisplayName("@PreUpdate deve preencher atualizadoEm")
    void preUpdateShouldSetUpdatedAt() {
        Cor cor = new Cor();
        assertThat(cor.getAtualizadoEm()).isNull();

        cor.preUpdate();

        assertThat(cor.getAtualizadoEm()).isNotNull();
    }

    @Test
    @DisplayName("Builder deve popular os campos principais")
    void builderShouldPopulateFields() {
        OffsetDateTime agora = OffsetDateTime.now();

        Cor cor = Cor.builder()
                .id(1L)
                .nome("Branco")
                .hex("#FFFFFF")
                .ativo(true)
                .criadoEm(agora)
                .atualizadoEm(agora)
                .build();

        assertThat(cor.getId()).isEqualTo(1L);
        assertThat(cor.getNome()).isEqualTo("Branco");
        assertThat(cor.getHex()).isEqualTo("#FFFFFF");
        assertThat(cor.getAtivo()).isTrue();
        assertThat(cor.getCriadoEm()).isEqualTo(agora);
        assertThat(cor.getAtualizadoEm()).isEqualTo(agora);
    }
}
