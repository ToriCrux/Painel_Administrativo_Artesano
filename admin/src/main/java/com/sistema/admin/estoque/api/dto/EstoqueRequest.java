package com.sistema.admin.estoque.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EstoqueRequest {

    @NotNull
    @Min(0)
    private Long saldo; // usado no PUT (ajustar saldo)

    // Para entrada e saída, vamos reaproveitar este mesmo DTO,
    // mas o campo será interpretado como "quantidade"
}
