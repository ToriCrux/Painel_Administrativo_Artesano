package com.sistema.pedido_service.infra;

import com.sistema.pedido_service.dominio.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // 🔹 Verifica se já existe um pedido com o mesmo código
    boolean existsByCodigo(String codigo);

    // 🔹 Busca um pedido pelo código (usado para adicionar itens ao mesmo pedido)
    Optional<Pedido> findByCodigo(String codigo);
}
