package com.sistema.estoque_service.infra;


import com.sistema.estoque_service.dominio.Estoque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    Optional<Estoque> findByProdutoId(Long produtoId);
    Page<Estoque> findByProdutoId(Long produtoId, Pageable pageable);
}
