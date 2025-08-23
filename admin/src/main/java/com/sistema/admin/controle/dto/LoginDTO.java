package com.sistema.admin.controle.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginDTO(
    @NotBlank @Email @Size(max = 160) String email,
    @NotBlank @Size(min = 6, max = 100) String senha


){}
