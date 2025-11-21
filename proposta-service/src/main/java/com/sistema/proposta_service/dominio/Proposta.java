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

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false, length = 120)
    private String nomeVendedor;

    @Column(nullable = false)
    private LocalDate dataProposta;

    @Column(nullable = false)
    private LocalDate dataValidade;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal total;

    public void adicionarProduto(ProdutoProposta produto) {
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
                .map(ProdutoProposta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
