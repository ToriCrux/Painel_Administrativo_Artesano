package com.sistema.proposta_service.dominio;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_proposta",
        indexes = {
                @Index(name = "ix_proposta_codigo", columnList = "codigo")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Proposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_proposta_cliente"))
    private Cliente cliente;

    @OneToMany(mappedBy = "proposta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProdutoProposta> produtos = new ArrayList<>();

    @Column(name="codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name="nome_vendedor", nullable = false, length = 120)
    private String nomeVendedor;

    @Column(name="data_proposta", nullable = false)
    private LocalDate dataProposta;

    @Column(name="data_validade", nullable = false)
    private LocalDate dataValidade;

    @Column(name="total", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    public void adicionarProduto(ProdutoProposta produto) {
        if (produto == null) return;
        produto.setProposta(this);
        produto.calcularSubtotal();
        this.produtos.add(produto);
        recalcularTotal();
    }

    public void removerProduto(ProdutoProposta produto) {
        this.produtos.remove(produto);
        recalcularTotal();
    }

    public void recalcularTotal() {
        this.total = produtos.stream()
                .map(p -> p.getSubtotal() == null ? BigDecimal.ZERO : p.getSubtotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Garante que todos os produtos apontem para esta proposta
     */
    public void amarrarProdutos() {
        if (produtos == null) return;
        for (ProdutoProposta p : produtos) {
            if (p != null) {
                p.setProposta(this);
                p.calcularSubtotal();
            }
        }
        recalcularTotal();
    }

    @PrePersist
    @PreUpdate
    private void prePersistUpdate() {
        amarrarProdutos();
    }
}
