package com.sistema.admin.proposta.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClienteDTO {

    @NotBlank
    @Size(max = 120)
    private String nome;

    @NotBlank
    @Size(max = 20)
    private String cpfCnpj;

    @Size(max = 15)
    private String telefone;

    @Email
    @Size(max = 120)
    private String email;

    @Size(max = 10)
    private String cep;

    @Size(max = 150)
    private String endereco;

    @Size(max = 80)
    private String bairro;

    @Size(max = 80)
    private String cidade;

    @Size(max = 2)
    private String uf;

    @Size(max = 150)
    private String referencia;

    @Size(max = 100)
    private String complemento;
}
