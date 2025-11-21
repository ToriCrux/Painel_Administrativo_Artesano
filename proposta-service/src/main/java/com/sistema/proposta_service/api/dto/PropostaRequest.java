package com.sistema.proposta_service.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PropostaRequest {

    @NotBlank
    private String codigo;

    @NotBlank
    private String nomeVendedor;

    @NotNull
    private LocalDate dataProposta;

    @NotNull
    private LocalDate dataValidade;

    @NotNull
    @Valid
    private ClienteDTO cliente;

    @NotNull
    @Valid
    private List<ProdutoPropostaDTO> produtos;
}
