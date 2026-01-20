package com.sistema.catalogoservice.catalogo.categoria.infra;

import com.sistema.catalogoservice.catalogo.categoria.dominio.Subcategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubcategoriaRepository extends JpaRepository<Subcategoria, Long> {

    Optional<Subcategoria> findByNomeIgnoreCase(String nome);

    List<Subcategoria> findByCategoriaId(Long categoriaId);

    @Query("""
        SELECT s FROM Subcategoria s
        JOIN FETCH s.categoria c
        WHERE LOWER(s.nome) = LOWER(:subcategoriaNome)
          AND LOWER(c.nome) = LOWER(:categoriaNome)
    """)
    Optional<Subcategoria> findByCategoriaENome(
            @Param("categoriaNome") String categoriaNome,
            @Param("subcategoriaNome") String subcategoriaNome
    );

    // ✅ 2ª query: fetch itens (evita MultipleBagFetchException)
    @Query("""
        SELECT DISTINCT s
        FROM Subcategoria s
        LEFT JOIN FETCH s.itens i
        WHERE s.categoria.id IN :categoriaIds
    """)
    List<Subcategoria> findAllByCategoriaIdInFetchItens(@Param("categoriaIds") List<Long> categoriaIds);
}
