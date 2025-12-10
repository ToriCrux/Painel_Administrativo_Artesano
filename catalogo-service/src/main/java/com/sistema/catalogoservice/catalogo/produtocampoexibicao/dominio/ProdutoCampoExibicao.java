package com.sistema.catalogoservice.catalogo.produtocampoexibicao.dominio;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "tb_produto_campo_exibicao",
        uniqueConstraints = @UniqueConstraint(columnNames = {"produto_id", "campo"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoCampoExibicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @Column(nullable = false, length = 50)
    private String campo;

    @Column(nullable = false)
    private Boolean visivel = true;

    @Column(nullable = false)
    private OffsetDateTime atualizadoEm;

    @PrePersist
    @PreUpdate
    public void preAtualizar() {
        this.atualizadoEm = OffsetDateTime.now();
    }
}
