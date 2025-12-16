package com.sistema.pedido_service.dominio;

import com.sistema.pedido_service.api.dto.StatusPedido;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "tb_pedido",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_pedido_codigo_produto",
                        columnNames = {"codigo", "produto_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ❌ não pode ser UNIQUE se uma proposta tiver mais de um item
    @Column(nullable = false, length = 50)
    private String codigo;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @Column(nullable = false, length = 120)
    private String nomeCliente;

    @Column(nullable = false, length = 150)
    private String produto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal precoUnitario;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPedido status;
}
