package com.sistema.admin.estoque.infra;

import com.sistema.admin.estoque.dominio.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {
    List<MovimentacaoEstoque> findByProdutoIdOrderByCriadoEmDesc(Long produtoId);
}
