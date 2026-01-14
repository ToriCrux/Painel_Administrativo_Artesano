package com.sistema.catalogoservice.catalogo.produto.infra;

import com.sistema.catalogoservice.catalogo.produto.dominio.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Optional<Produto> findByCodigoIgnoreCase(String codigo);

    @EntityGraph(attributePaths = {
            "itensCategoria",
            "itensCategoria.subcategoria",
            "itensCategoria.subcategoria.categoria",
            "cores",
            "cores.grupo"
    })
    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {
            "itensCategoria",
            "itensCategoria.subcategoria",
            "itensCategoria.subcategoria.categoria",
            "cores",
            "cores.grupo"
    })
    Page<Produto> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {
            "itensCategoria",
            "itensCategoria.subcategoria",
            "itensCategoria.subcategoria.categoria",
            "cores",
            "cores.grupo"
    })
    Optional<Produto> findById(Long id);

    @Query("SELECT p.id FROM Produto p WHERE p.ativo = true")
    List<Long> findAllIdsAtivos();

    // 🔹 Ajustes para nova estrutura de itensCategoria (ManyToMany)
    @Query("""
        SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
        FROM Produto p
        JOIN p.itensCategoria ic
        JOIN ic.subcategoria s
        JOIN s.categoria c
        WHERE c.id = :categoriaId
    """)
    boolean existsByCategoriaId(Long categoriaId);

    @Query("""
        SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
        FROM Produto p
        JOIN p.itensCategoria ic
        JOIN ic.subcategoria s
        WHERE s.id = :subcategoriaId
    """)
    boolean existsBySubcategoriaId(Long subcategoriaId);

    @Query("""
        SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
        FROM Produto p
        JOIN p.itensCategoria ic
        WHERE ic.id = :itemCategoriaId
    """)
    boolean existsByItemCategoriaId(Long itemCategoriaId);
}
