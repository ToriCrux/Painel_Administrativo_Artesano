package com.sistema.estoque_service.dominio;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "tb_estoque",
        uniqueConstraints = @UniqueConstraint(name = "uk_estoque_produto", columnNames = "produto_id"),
        indexes = @Index(name = "ix_estoque_produto", columnList = "produto_id")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @Column(name = "produto_codigo", nullable = false)
    private String produtoCodigo = "—";

    @Column(name = "produto_nome", nullable = false)
    private String produtoNome = "(Produto removido)";

    @Column(name = "saldo", nullable = false)
    private long saldo = 0L;

    @Version
    private long versao;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @Column(nullable = false)
    private OffsetDateTime atualizadoEm;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true; // 🔹 true = produto ativo, false = produto removido/excluído

    @PrePersist
    public void prePersist() {
        var now = OffsetDateTime.now();
        this.criadoEm = now;
        this.atualizadoEm = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = OffsetDateTime.now();
    }

    public void aumentar(long qtd) {
        validarQuantidade(qtd);
        this.saldo += qtd;
    }

    public void baixar(long qtd) {
        validarQuantidade(qtd);
        if (this.saldo - qtd < 0) {
            throw new IllegalStateException("Saldo insuficiente para baixa");
        }
        this.saldo -= qtd;
    }

    public void ajustar(long novoSaldo) {
        if (novoSaldo < 0) {
            throw new IllegalArgumentException("Saldo não pode ser negativo");
        }
        this.saldo = novoSaldo;
    }

    public void marcarComoExcluido() {
        this.ativo = false;
        this.produtoNome = "(Produto removido)";
        this.produtoCodigo = "—";
    }

    private void validarQuantidade(long qtd) {
        if (qtd <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }
    }
}
