
package com.sistema.proposta_service.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes da lógica de negócio da entidade Proposta.
 */
class PropostaTest {

    private Proposta novaPropostaVazia() {
        return Proposta.builder()
                .codigo("PROP-1")
                .nomeVendedor("Vendedor")
                .dataProposta(LocalDate.of(2025, 1, 1))
                .dataValidade(LocalDate.of(2025, 1, 15))
                .cliente(Cliente.builder()
                        .nome("Cliente")
                        .cpfCnpj("12345678900")
                        .telefone("61999990000")
                        .email("cliente@teste.com")
                        .build())
                .build();
    }

    @Test
    @DisplayName("adicionarProduto deve somar o subtotal ao total da proposta")
    void adicionarProduto_deveAtualizarTotal() {
        Proposta proposta = novaPropostaVazia();

        ProdutoProposta p1 = ProdutoProposta.builder()
                .codigoProduto("P1")
                .nomeProduto("Produto 1")
                .quantidade(1)
                .precoUnitario(new BigDecimal("10.00"))
                .build();
        p1.calcularSubtotal();

        ProdutoProposta p2 = ProdutoProposta.builder()
                .codigoProduto("P2")
                .nomeProduto("Produto 2")
                .quantidade(2)
                .precoUnitario(new BigDecimal("5.00"))
                .build();
        p2.calcularSubtotal();

        proposta.adicionarProduto(p1);
        proposta.adicionarProduto(p2);

        assertThat(proposta.getProdutos()).hasSize(2);
        assertThat(proposta.getTotal()).isEqualTo(new BigDecimal("20.00"));
    }

    @Test
    @DisplayName("removerProduto deve subtrair o subtotal e atualizar o total")
    void removerProduto_deveAtualizarTotal() {
        Proposta proposta = novaPropostaVazia();

        ProdutoProposta p1 = ProdutoProposta.builder()
                .codigoProduto("P1")
                .nomeProduto("Produto 1")
                .quantidade(1)
                .precoUnitario(new BigDecimal("10.00"))
                .build();
        p1.calcularSubtotal();

        ProdutoProposta p2 = ProdutoProposta.builder()
                .codigoProduto("P2")
                .nomeProduto("Produto 2")
                .quantidade(2)
                .precoUnitario(new BigDecimal("5.00"))
                .build();
        p2.calcularSubtotal();

        proposta.adicionarProduto(p1);
        proposta.adicionarProduto(p2);
        assertThat(proposta.getTotal()).isEqualTo(new BigDecimal("20.00"));

        proposta.removerProduto(p1);

        assertThat(proposta.getProdutos()).containsExactly(p2);
        assertThat(proposta.getTotal()).isEqualTo(new BigDecimal("10.00"));
    }
}
