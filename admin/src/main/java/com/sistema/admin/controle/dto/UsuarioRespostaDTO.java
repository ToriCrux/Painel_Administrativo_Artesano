package com.sistema.admin.controle.dto;

import com.sistema.admin.identidade.dominio.Role;

import java.util.Set;

public record UsuarioRespostaDTO (
        Long id, String nome, String email, Boolean ativo, Set<Role> roles

){}
