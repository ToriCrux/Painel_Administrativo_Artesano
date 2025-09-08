package com.sistema.admin.controle.dto.loginEcadastro;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroDTO(
    @NotBlank @Size(max = 120) String nome,
    @NotBlank @Email @Size(max = 160) String email,
    @NotBlank @Size(min = 6, max = 100) String senha
){}
