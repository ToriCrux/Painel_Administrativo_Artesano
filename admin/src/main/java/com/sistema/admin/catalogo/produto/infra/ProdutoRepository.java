package com.sistema.admin.catalogo.produto.infra;

import com.sistema.admin.catalogo.produto.dominio.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Optional<Produto> findByCodigoIgnoreCase(String codigo);

    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
