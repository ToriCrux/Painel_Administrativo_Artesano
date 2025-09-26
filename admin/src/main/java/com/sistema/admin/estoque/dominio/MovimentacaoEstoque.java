package com.sistema.admin.estoque.dominio;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tb_movimentacao_estoque")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long produtoId;

    @Column(nullable = false)
    private String tipo; // ENTRADA, SAIDA, AJUSTE, CRIACAO

    @Column(nullable = false)
    private Long quantidade;

    @Column(nullable = false)
    private Long saldoAnterior;

    @Column(nullable = false)
    private Long saldoNovo;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @PrePersist
    public void prePersist() {
        this.criadoEm = OffsetDateTime.now();
    }
}
