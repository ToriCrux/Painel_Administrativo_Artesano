package com.sistema.proposta_service.infra;

import com.sistema.proposta_service.dominio.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // ✅ novo (busca determinística)
    Optional<Cliente> findByCpfCnpjHash(String cpfCnpjHash);

    // ✅ compat durante migração (enquanto ainda existir cpf_cnpj legado preenchido)
    Optional<Cliente> findByCpfCnpjLegacy(String cpfCnpjLegacy);
}
