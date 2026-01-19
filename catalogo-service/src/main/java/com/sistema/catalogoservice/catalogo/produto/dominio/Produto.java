package com.sistema.catalogoservice.catalogo.produto.dominio;

import com.sistema.catalogoservice.catalogo.categoria.dominio.ItemCategoria;
import com.sistema.catalogoservice.catalogo.cor.dominio.Cor;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "tb_produto")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String codigo;

    @Column(nullable = false, length = 120)
    private String nome;

    // ✅ Suporte a múltiplas categorias e itens
    @ManyToMany
    @JoinTable(
            name = "tb_produto_item_categoria",
            joinColumns = @JoinColumn(name = "produto_id"),
            inverseJoinColumns = @JoinColumn(name = "item_categoria_id")
    )
    @Builder.Default
    private Set<ItemCategoria> itensCategoria = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "tb_produto_cor",
            joinColumns = @JoinColumn(name = "produto_id"),
            inverseJoinColumns = @JoinColumn(name = "cor_id")
    )
    @Builder.Default
    private Set<Cor> cores = new HashSet<>();


    // ✅ NOVO: Detalhes técnicos flexíveis (JSONB)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "detalhes_tecnicos", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, String> detalhesTecnicos = new LinkedHashMap<>();

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal precoUnitario;

    @Column(length = 500)
    private String descricao;

    @Builder.Default
    private Boolean ativo = true;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @Column(nullable = false)
    private OffsetDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        this.criadoEm = OffsetDateTime.now();
        this.atualizadoEm = OffsetDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = OffsetDateTime.now();
    }
}
