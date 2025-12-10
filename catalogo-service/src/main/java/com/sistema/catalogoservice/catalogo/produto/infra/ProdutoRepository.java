package com.sistema.catalogoservice.catalogo.produto.infra;


import com.sistema.catalogoservice.catalogo.produto.dominio.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Optional<Produto> findByCodigoIgnoreCase(String codigo);

    @EntityGraph(attributePaths = {"categoria", "cores"})
    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"categoria", "cores"})
    Page<Produto> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"categoria", "cores"})
    Optional<Produto> findById(Long id);

    @Query("SELECT p.id FROM Produto p WHERE p.ativo = true")
    List<Long> findAllIdsAtivos();
}
