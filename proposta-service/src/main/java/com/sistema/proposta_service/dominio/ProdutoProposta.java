package com.sistema.proposta_service.dominio;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "tb_produto_proposta",
        indexes = {
                @Index(name = "ix_produto_proposta_codigo", columnList = "codigo_produto"),
                @Index(name = "ix_produto_proposta_proposta", columnList = "proposta_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class ProdutoProposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "proposta_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_produto_proposta_proposta")
    )
    private Proposta proposta;

    @Column(name = "codigo_produto", nullable = false, length = 50)
    private String codigoProduto;

    @Column(name = "nome_produto", nullable = false, length = 150)
    private String nomeProduto;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 15, scale = 2)
    private BigDecimal precoUnitario;

    @Column(name = "subtotal", nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal;

    /**
     * Calcula o subtotal automaticamente
     */
    public void calcularSubtotal() {
        if (quantidade != null && precoUnitario != null) {
            this.subtotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }

    @PrePersist
    @PreUpdate
    private void prePersistUpdate() {
        calcularSubtotal();
    }
}
