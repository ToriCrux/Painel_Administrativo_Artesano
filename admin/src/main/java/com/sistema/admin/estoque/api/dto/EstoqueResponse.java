package com.sistema.admin.estoque.api.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.OffsetDateTime;

@Builder
@Getter
public class EstoqueResponse {
    private Long produtoId;
    private String produtoCodigo;
    private String produtoNome;
    private Long saldo;
    private Long versao;
    private OffsetDateTime atualizadoEm;
}
