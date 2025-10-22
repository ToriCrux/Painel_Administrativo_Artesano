package com.sistema.admin.estoque.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class MovimentacaoEstoqueResponse {
    private Long id;
    private Long produtoId;
    private String tipo;
    private Long quantidade;
    private Long saldoFinal;
    private OffsetDateTime criadoEm;
}
