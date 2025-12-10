package com.sistema.catalogoservice.catalogo.produtocampoexibicao.dominio;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_campo_exibicao_padrao")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampoExibicaoPadrao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nome;

    @Column(name = "visivel_padrao", nullable = false)
    private Boolean visivelPadrao = true;
}
