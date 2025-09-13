package com.sistema.admin.Estoque.dominio;

import com.sistema.admin.catalogo.produto.dominio.Produto;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tb_estoque")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relacionamento direto com Produto.
     * Cada produto terá no máximo 1 estoque vinculado.
     */
    @OneToOne(optional = false)
    @JoinColumn(name = "produto_id", nullable = false, unique = true)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidadeAtual;

    @Column(nullable = false)
    private OffsetDateTime atualizadoEm;

    /**
     * Define data de criação e atualização automaticamente.
     */
    @PrePersist
    public void prePersist() {
        this.atualizadoEm = OffsetDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = OffsetDateTime.now();
    }
}

