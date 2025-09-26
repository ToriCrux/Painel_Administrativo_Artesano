package com.sistema.admin.estoque.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Builder
@Getter
public class EstoqueResponse {
    private Long produtoId;
    private Long saldo;
    private Long versao;
    private OffsetDateTime atualizadoEm;
}
