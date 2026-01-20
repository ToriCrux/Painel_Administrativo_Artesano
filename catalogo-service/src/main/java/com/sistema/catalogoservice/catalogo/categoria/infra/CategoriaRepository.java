package com.sistema.catalogoservice.catalogo.categoria.infra;

import com.sistema.catalogoservice.catalogo.categoria.dominio.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findByNomeIgnoreCase(String nome);

    // ✅ fetch só de subcategorias (1 bag)
    @Query("""
        SELECT DISTINCT c
        FROM Categoria c
        LEFT JOIN FETCH c.subcategorias s
        WHERE c.ativo = true
    """)
    List<Categoria> findAllAtivasComSubcategorias();
}
