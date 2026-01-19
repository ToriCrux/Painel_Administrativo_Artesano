package com.sistema.catalogoservice.catalogo.produtocampoexibicao.dominio;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "tb_produto_exibicao_exclusao",
        uniqueConstraints = @UniqueConstraint(columnNames = {"produto_id", "tipo", "ref_id", "chave"})
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoExibicaoExclusao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoExclusaoExibicao tipo;

    @Column(name = "ref_id")
    private Long refId;

    @Column(length = 120)
    private String chave;

    @Column(nullable = false)
    private OffsetDateTime atualizadoEm;

    @PrePersist
    @PreUpdate
    public void preAtualizar() {
        this.atualizadoEm = OffsetDateTime.now();
    }
}
