package com.sistema.admin.catalogo.cor.infra;


import com.sistema.admin.catalogo.cor.dominio.Cor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface CorRepository extends JpaRepository<Cor, Long> {
    Optional<Cor> findByNomeIgnoreCase(String nome);
    Page<Cor> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
