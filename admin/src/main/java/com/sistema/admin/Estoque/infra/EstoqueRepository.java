package com.sistema.admin.Estoque.infra;


import com.sistema.admin.Estoque.dominio.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    /**
     * Busca o estoque vinculado a um produto específico.
     * Útil para consultar ou atualizar o estoque a partir do produto.
     */
    Optional<Estoque> findByProdutoId(Long produtoId);
}
