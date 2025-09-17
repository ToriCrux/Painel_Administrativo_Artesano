package com.sistema.admin.estoque.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class EstoqueRequest {
    @NotNull
    @Min(0)
    private Long saldo;

    public Long getSaldo() { return saldo; }
    public void setSaldo(Long saldo) { this.saldo = saldo; }
}
