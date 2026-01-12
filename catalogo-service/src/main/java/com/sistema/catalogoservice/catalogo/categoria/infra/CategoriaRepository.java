package com.sistema.catalogoservice.catalogo.categoria.infra;

import com.sistema.catalogoservice.catalogo.categoria.dominio.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findByNomeIgnoreCase(String nome);

    // ✅ Busca com nome (carrega subcategorias)
    @EntityGraph(attributePaths = {"subcategorias"})
    Page<Categoria> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    // ✅ Corrigido: "subcategorias" estava escrito errado
    @EntityGraph(attributePaths = {"subcategorias"})
    Page<Categoria> findAll(Pageable pageable);

    // ✅ Carrega subcategorias ao buscar por ID
    @EntityGraph(attributePaths = {"subcategorias"})
    Optional<Categoria> findById(Long id);

    // ✅ Retorna apenas categorias principais (sem pai)
    @EntityGraph(attributePaths = {"subcategorias"})
    Page<Categoria> findByCategoriaPaiIsNull(Pageable pageable);

    // ✅ Busca subcategorias diretas de uma categoria pai
    List<Categoria> findByCategoriaPaiId(Long id);
}
