package com.sistema.admin.catalogo.infra;

import com.sistema.admin.catalogo.dominio.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNomeIgnoreCase(String nome);
    Page<Categoria> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
