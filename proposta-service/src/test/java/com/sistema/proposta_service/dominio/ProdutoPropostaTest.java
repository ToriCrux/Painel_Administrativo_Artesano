
package com.sistema.proposta_service.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes simples para a entidade ProdutoProposta.
 */
class ProdutoPropostaTest {

    @Test
    @DisplayName("calcularSubtotal deve multiplicar quantidade pelo preço unitário")
    void calcularSubtotal_ok() {
        ProdutoProposta produto = ProdutoProposta.builder()
                .codigoProduto("P1")
                .nomeProduto("Produto 1")
                .quantidade(3)
                .precoUnitario(new BigDecimal("9.90"))
                .build();

        produto.calcularSubtotal();

        assertThat(produto.getSubtotal()).isEqualTo(new BigDecimal("29.70"));
    }

    @Test
    @DisplayName("calcularSubtotal deve definir subtotal como ZERO quando quantidade ou preço forem nulos")
    void calcularSubtotal_quandoValoresNulos() {
        ProdutoProposta produtoSemQuantidade = ProdutoProposta.builder()
                .codigoProduto("P1")
                .nomeProduto("Produto 1")
                .quantidade(null)
                .precoUnitario(new BigDecimal("10.00"))
                .build();
        produtoSemQuantidade.calcularSubtotal();
        assertThat(produtoSemQuantidade.getSubtotal()).isEqualTo(BigDecimal.ZERO);

        ProdutoProposta produtoSemPreco = ProdutoProposta.builder()
                .codigoProduto("P2")
                .nomeProduto("Produto 2")
                .quantidade(2)
                .precoUnitario(null)
                .build();
        produtoSemPreco.calcularSubtotal();
        assertThat(produtoSemPreco.getSubtotal()).isEqualTo(BigDecimal.ZERO);
    }
}
