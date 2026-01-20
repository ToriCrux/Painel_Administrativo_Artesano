package com.sistema.catalogoservice.catalogo.cor.infra;

import com.sistema.catalogoservice.catalogo.cor.dominio.Cor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CorRepository extends JpaRepository<Cor, Long> {

    boolean existsByGrupoIsNullAndNomeIgnoreCase(String nome);
    boolean existsByGrupoIsNullAndNomeIgnoreCaseAndIdNot(String nome, Long id);

    boolean existsByGrupoIdAndNomeIgnoreCase(Long grupoId, String nome);

    // ✅ VOLTOU: para não quebrar outros services (ex: ProdutoService)
    Optional<Cor> findByNomeIgnoreCase(String nome);

    // ✅ (recomendado) se você quiser usar no ProdutoService futuramente
    Optional<Cor> findByGrupoIsNullAndNomeIgnoreCase(String nome);

    @EntityGraph(attributePaths = {"subcores"})
    Page<Cor> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    @EntityGraph(attributePaths = {"subcores"})
    Optional<Cor> findById(Long id);

    @EntityGraph(attributePaths = {"subcores"})
    Page<Cor> findByGrupoIsNull(Pageable pageable);

    List<Cor> findByGrupoId(Long id);

    // ======================================================
    // ✅ NOVO: Busca subcores de um grupo por lista de nomes
    // - Espera que os nomes já venham em lowercase
    // ======================================================
    @Query("""
        SELECT c
        FROM Cor c
        WHERE c.grupo.id = :grupoId
          AND LOWER(c.nome) IN :nomesLower
    """)
    List<Cor> findSubcoresDoGrupoPorNomes(
            @Param("grupoId") Long grupoId,
            @Param("nomesLower") List<String> nomesLower
    );

    @Query("""
        SELECT DISTINCT c
        FROM Cor c
        LEFT JOIN FETCH c.subcores s
        WHERE c.grupo IS NULL
          AND c.ativo = true
    """)
    List<Cor> findGruposAtivosComSubcores();
}
