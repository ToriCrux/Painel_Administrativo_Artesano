package com.sistema.admin.catalogo.produtoimagem.dominio;

import com.sistema.admin.catalogo.produto.dominio.Produto;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tb_produto_imagem")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ProdutoImagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "data", columnDefinition = "bytea", nullable = false)
    private byte[] data;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "nome_arquivo", length = 255)
    private String nomeArquivo;

    @Column(name = "tamanho_bytes")
    private Long tamanhoBytes;

    @Column(name = "principal")
    private Boolean principal;

    @Column(name = "ordem")
    private Integer ordem;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
		this.criadoEm = OffsetDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = OffsetDateTime.now();
    }
}