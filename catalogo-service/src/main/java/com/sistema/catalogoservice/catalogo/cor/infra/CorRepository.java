package com.sistema.catalogoservice.catalogo.cor.infra;

import com.sistema.catalogoservice.catalogo.cor.dominio.Cor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CorRepository extends JpaRepository<Cor, Long> {

    Optional<Cor> findByNomeIgnoreCase(String nome);

    @EntityGraph(attributePaths = {"subcores"})
    Page<Cor> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    @EntityGraph(attributePaths = {"subcores"})
    Page<Cor> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"subcores"})
    Optional<Cor> findById(Long id);

    // ✅ Apenas grupos principais
    @EntityGraph(attributePaths = {"subcores"})
    Page<Cor> findByGrupoIsNull(Pageable pageable);

    List<Cor> findByGrupoId(Long id);
}
