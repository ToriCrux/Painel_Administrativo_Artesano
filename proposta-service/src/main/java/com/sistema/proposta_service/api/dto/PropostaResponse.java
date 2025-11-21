package com.sistema.proposta_service.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class PropostaResponse {

    private Long id;
    private String codigo;
    private String nomeVendedor;
    private LocalDate dataProposta;
    private LocalDate dataValidade;
    private BigDecimal total;
    private ClienteDTO cliente;
    private List<ProdutoPropostaDTO> produtos;
}
