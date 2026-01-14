package com.sistema.catalogoservice.catalogo.categoria.infra;

import com.sistema.catalogoservice.catalogo.categoria.dominio.ItemCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemCategoriaRepository extends JpaRepository<ItemCategoria, Long> {

    @Query("""
        SELECT i FROM ItemCategoria i
        JOIN FETCH i.subcategoria s
        JOIN FETCH s.categoria c
        WHERE LOWER(c.nome) = LOWER(:categoriaNome)
          AND LOWER(s.nome) = LOWER(:subcategoriaNome)
          AND LOWER(i.nome) = LOWER(:itemNome)
    """)
    Optional<ItemCategoria> findByHierarquia(
            @Param("categoriaNome") String categoriaNome,
            @Param("subcategoriaNome") String subcategoriaNome,
            @Param("itemNome") String itemNome
    );
}
