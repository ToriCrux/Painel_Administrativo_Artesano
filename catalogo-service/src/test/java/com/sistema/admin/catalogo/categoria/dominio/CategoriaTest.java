package com.sistema.admin.catalogo.categoria.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CategoriaTest {

    @Test
    @DisplayName("@PrePersist deve preencher criadoEm")
    void prePersistShouldSetCreatedAt() {
        Categoria categoria = new Categoria();
        assertThat(categoria.getCriadoEm()).isNull();

        categoria.prePersist();

        assertThat(categoria.getCriadoEm()).isNotNull();
    }

    @Test
    @DisplayName("@PreUpdate deve preencher atualizadoEm")
    void preUpdateShouldSetUpdatedAt() {
        Categoria categoria = new Categoria();
        assertThat(categoria.getAtualizadoEm()).isNull();

        categoria.preUpdate();

        assertThat(categoria.getAtualizadoEm()).isNotNull();
    }

    @Test
    @DisplayName("Builder deve popular os campos principais")
    void builderShouldPopulateFields() {
        OffsetDateTime agora = OffsetDateTime.now();

        Categoria categoria = Categoria.builder()
                .id(1L)
                .nome("Periféricos")
                .ativo(true)
                .criadoEm(agora)
                .atualizadoEm(agora)
                .build();

        assertThat(categoria.getId()).isEqualTo(1L);
        assertThat(categoria.getNome()).isEqualTo("Periféricos");
        assertThat(categoria.getAtivo()).isTrue();
        assertThat(categoria.getCriadoEm()).isEqualTo(agora);
        assertThat(categoria.getAtualizadoEm()).isEqualTo(agora);
    }
}
