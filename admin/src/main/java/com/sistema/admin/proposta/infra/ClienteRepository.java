package com.sistema.admin.proposta.infra;

import com.sistema.admin.proposta.dominio.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCpfCnpj(String cpfCnpj);
}
