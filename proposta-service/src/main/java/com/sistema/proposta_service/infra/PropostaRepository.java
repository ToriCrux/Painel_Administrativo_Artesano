package com.sistema.proposta_service.infra;

import com.sistema.proposta_service.dominio.Proposta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PropostaRepository extends JpaRepository<Proposta, Long> {

    @EntityGraph(attributePaths = {"cliente", "produtos"})
    Page<Proposta> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"cliente", "produtos"})
    Optional<Proposta> findByCodigo(String codigo);

    @EntityGraph(attributePaths = {"cliente", "produtos"})
    Page<Proposta> findByNomeVendedorContainingIgnoreCase(String nomeVendedor, Pageable pageable);
}
