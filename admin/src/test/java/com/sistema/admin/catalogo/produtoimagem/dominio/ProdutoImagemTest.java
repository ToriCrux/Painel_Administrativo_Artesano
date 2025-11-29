package com.sistema.admin.catalogo.produtoimagem.dominio;

import com.sistema.admin.catalogo.produto.dominio.Produto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ProdutoImagemTest {

    @Test
    @DisplayName("@PrePersist deve preencher criadoEm")
    void prePersistShouldSetCreatedAt() {
        ProdutoImagem imagem = new ProdutoImagem();
        assertThat(imagem.getCriadoEm()).isNull();

        imagem.prePersist();

        assertThat(imagem.getCriadoEm()).isNotNull();
    }

    @Test
    @DisplayName("@PreUpdate deve preencher atualizadoEm")
    void preUpdateShouldSetUpdatedAt() {
        ProdutoImagem imagem = new ProdutoImagem();
        assertThat(imagem.getAtualizadoEm()).isNull();

        imagem.preUpdate();

        assertThat(imagem.getAtualizadoEm()).isNotNull();
    }

    @Test
    @DisplayName("Builder deve permitir associar produto e campos principais")
    void builderShouldPopulateFields() {
        Produto produto = Produto.builder().id(1L).build();
        OffsetDateTime agora = OffsetDateTime.now();

        ProdutoImagem imagem = ProdutoImagem.builder()
                .id(10L)
                .produto(produto)
                .contentType("image/png")
                .nomeArquivo("imagem.png")
                .tamanhoBytes(1234L)
                .principal(true)
                .ordem(1)
                .criadoEm(agora)
                .atualizadoEm(agora)
                .build();

        assertThat(imagem.getId()).isEqualTo(10L);
        assertThat(imagem.getProduto()).isSameAs(produto);
        assertThat(imagem.getContentType()).isEqualTo("image/png");
        assertThat(imagem.getNomeArquivo()).isEqualTo("imagem.png");
        assertThat(imagem.getTamanhoBytes()).isEqualTo(1234L);
        assertThat(imagem.getPrincipal()).isTrue();
        assertThat(imagem.getOrdem()).isEqualTo(1);
        assertThat(imagem.getCriadoEm()).isEqualTo(agora);
        assertThat(imagem.getAtualizadoEm()).isEqualTo(agora);
    }
}
