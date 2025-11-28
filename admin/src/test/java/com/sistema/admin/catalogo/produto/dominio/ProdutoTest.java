package com.sistema.admin.catalogo.produto.dominio;

import com.sistema.admin.catalogo.categoria.dominio.Categoria;
import com.sistema.admin.catalogo.cor.dominio.Cor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProdutoTest {

    @Test
    @DisplayName("@PrePersist deve preencher criadoEm")
    void prePersistShouldSetCreatedAt() {
        Produto produto = new Produto();
        assertThat(produto.getCriadoEm()).isNull();

        produto.prePersist();

        assertThat(produto.getCriadoEm()).isNotNull();
    }

    @Test
    @DisplayName("@PreUpdate deve preencher atualizadoEm")
    void preUpdateShouldSetUpdatedAt() {
        Produto produto = new Produto();
        assertThat(produto.getAtualizadoEm()).isNull();

        produto.preUpdate();

        assertThat(produto.getAtualizadoEm()).isNotNull();
    }

    @Test
    @DisplayName("Builder deve popular campos principais e relacionamentos")
    void builderShouldPopulateFields() {
        Categoria categoria = Categoria.builder().id(10L).nome("Periféricos").build();
        Cor cor = Cor.builder().id(1L).nome("Branco").hex("#FFFFFF").build();

        Produto produto = Produto.builder()
                .id(5L)
                .codigo("COD-123")
                .nome("Mouse Gamer")
                .categoria(categoria)
                .cores(Set.of(cor))
                .medidas("10x5 cm")
                .precoUnitario(new BigDecimal("199.90"))
                .ativo(true)
                .descricao("Mouse com RGB")
                .build();

        assertThat(produto.getId()).isEqualTo(5L);
        assertThat(produto.getCodigo()).isEqualTo("COD-123");
        assertThat(produto.getNome()).isEqualTo("Mouse Gamer");
        assertThat(produto.getCategoria()).isSameAs(categoria);
        assertThat(produto.getCores()).containsExactly(cor);
        assertThat(produto.getPrecoUnitario()).isEqualTo(new BigDecimal("199.90"));
        assertThat(produto.getAtivo()).isTrue();
        assertThat(produto.getDescricao()).isEqualTo("Mouse com RGB");
    }
}
