package com.sistema.admin.auth.infra;

import com.sistema.admin.auth.dominio.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Código que e responsável por manipular os dados de role do repository.
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNome(String nome);
}
