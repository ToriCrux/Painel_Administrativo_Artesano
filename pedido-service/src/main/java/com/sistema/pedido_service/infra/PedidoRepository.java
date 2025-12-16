package com.sistema.pedido_service.infra;

import com.sistema.pedido_service.dominio.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    boolean existsByCodigoAndProdutoId(String codigo, Long produtoId);
}
