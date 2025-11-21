package com.sistema.estoque_service.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EstoqueRequest {

    @NotNull
    @Min(0)
    private Long saldo;


}
