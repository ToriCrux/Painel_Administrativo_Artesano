package com.sistema.admin.controle.dto.produto;

import com.sistema.admin.controle.dto.categoria.CategoriaResponseDTO;
import com.sistema.admin.controle.dto.cor.CorResponseDTO;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

public record ProdutoResponseDTO(
        Long id,
        String codigo,
        String nome,
        CategoriaResponseDTO categoria,   // aqui já devolvemos os detalhes da categoria
        Set<CorResponseDTO> cores,        // e aqui os detalhes das cores
        String medidas,
        BigDecimal precoUnitario,
        Boolean ativo,
        OffsetDateTime criadoEm,
        OffsetDateTime atualizadoEm
) {}
