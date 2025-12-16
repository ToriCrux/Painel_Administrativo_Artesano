package com.sistema.proposta_service.dominio;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_cliente",
        indexes = {
                @Index(name = "ix_cliente_cpf_cnpj", columnList = "cpf_cnpj"),
                @Index(name = "ix_cliente_nome", columnList = "nome")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="nome", nullable = false, length = 120)
    private String nome;

    @Column(name="cpf_cnpj", nullable = false, unique = true, length = 20)
    private String cpfCnpj;

    @Column(name="telefone", length = 15)
    private String telefone;

    @Column(name="email", nullable = false, length = 120)
    private String email;

    @Column(name="cep", length = 10)
    private String cep;

    @Column(name="endereco", length = 150)
    private String endereco;

    @Column(name="numero", length = 20)
    private String numero;

    @Column(name="complemento", length = 100)
    private String complemento;

    @Column(name="bairro", length = 80)
    private String bairro;

    @Column(name="cidade", length = 80)
    private String cidade;

    @Column(name="uf", length = 2)
    private String uf;

    @Column(name="referencia", length = 150)
    private String referencia;

    /**
     * Atualiza apenas os campos que vieram preenchidos (sem setters Lombok).
     */
    public void aplicarAtualizacoesSeVieram(Cliente recebido) {
        if (recebido == null) return;

        if (recebido.nome != null) this.nome = recebido.nome;
        if (recebido.telefone != null) this.telefone = recebido.telefone;
        if (recebido.email != null) this.email = recebido.email;
        if (recebido.cep != null) this.cep = recebido.cep;
        if (recebido.endereco != null) this.endereco = recebido.endereco;
        if (recebido.numero != null) this.numero = recebido.numero;
        if (recebido.complemento != null) this.complemento = recebido.complemento;
        if (recebido.bairro != null) this.bairro = recebido.bairro;
        if (recebido.cidade != null) this.cidade = recebido.cidade;
        if (recebido.uf != null) this.uf = recebido.uf;
        if (recebido.referencia != null) this.referencia = recebido.referencia;
    }

    /**
     * Cria um novo cliente a partir do payload recebido.
     * (CPF/CNPJ + nome + email são essenciais pelo seu schema)
     */
    public static Cliente novo(Cliente recebido) {
        return Cliente.builder()
                .nome(recebido.getNome())
                .cpfCnpj(recebido.getCpfCnpj())
                .telefone(recebido.getTelefone())
                .email(recebido.getEmail())
                .cep(recebido.getCep())
                .endereco(recebido.getEndereco())
                .numero(recebido.getNumero())
                .complemento(recebido.getComplemento())
                .bairro(recebido.getBairro())
                .cidade(recebido.getCidade())
                .uf(recebido.getUf())
                .referencia(recebido.getReferencia())
                .build();
    }
}
