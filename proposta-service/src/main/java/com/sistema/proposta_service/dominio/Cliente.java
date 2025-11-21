package com.sistema.proposta_service.dominio;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_cliente",
        indexes = {
                @Index(name = "ix_cliente_cpf_cnpj", columnList = "cpfCnpj"),
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

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 20)
    private String cpfCnpj;

    @Column(length = 15)
    private String telefone;

    @Column(length = 120)
    private String email;

    @Column(length = 10)
    private String cep;

    @Column(length = 150)
    private String endereco;

    @Column(length = 80)
    private String bairro;

    @Column(length = 80)
    private String cidade;

    @Column(length = 2)
    private String uf;

    @Column(length = 150)
    private String referencia;

    @Column(length = 100)
    private String complemento;
}
