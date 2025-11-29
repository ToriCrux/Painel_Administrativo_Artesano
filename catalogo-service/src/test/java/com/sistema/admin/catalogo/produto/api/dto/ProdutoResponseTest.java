package com.sistema.admin.catalogo.produto.api.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.admin.catalogo.categoria.api.dto.CategoriaResponse;
import com.sistema.admin.catalogo.cor.api.dto.CorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProdutoResponseTest {

    @Test
    @DisplayName("ProdutoResponse deve serializar e desserializar corretamente")
    void shouldSerializeAndDeserialize() throws Exception {
		ObjectMapper om = new ObjectMapper().findAndRegisterModules();

        OffsetDateTime criadoEm = OffsetDateTime.parse("2025-09-25T18:00:00-03:00");
        OffsetDateTime atualizadoEm = OffsetDateTime.parse("2025-09-25T18:30:00-03:00");

        CategoriaResponse categoria = new CategoriaResponse(
                10L, "Periféricos", true, criadoEm, atualizadoEm
        );
        CorResponse cor = new CorResponse(
                1L, "Branco", "#FFFFFF", true, criadoEm, atualizadoEm
        );

        ProdutoResponse original = new ProdutoResponse(
                5L,
                "COD-123",
                "Mouse Gamer",
                categoria,
                Set.of(cor),
                "10x5 cm",
                new BigDecimal("199.90"),
                true,
                "http://cdn/imagem.png",
                "Mouse top",
                criadoEm,
                atualizadoEm
        );

        String json = om.writeValueAsString(original);

		assertThat(json)
				.contains("\"id\":5")
				.contains("\"codigo\":\"COD-123\"")
				.contains("\"nome\":\"Mouse Gamer\"")
				.contains("\"precoUnitario\":199.90")
				.contains("\"ativo\":true")
				.contains("\"imagemPrincipalUrl\":\"http://cdn/imagem.png\"")
				.contains("\"descricao\":\"Mouse top\"");

		ProdutoResponse back = om.readValue(json, ProdutoResponse.class);

		assertThat(back.id()).isEqualTo(original.id());
		assertThat(back.codigo()).isEqualTo(original.codigo());
		assertThat(back.nome()).isEqualTo(original.nome());

		assertThat(back.criadoEm().toInstant()).isEqualTo(original.criadoEm().toInstant());
		assertThat(back.atualizadoEm().toInstant()).isEqualTo(original.atualizadoEm().toInstant());

		assertThat(back.categoria().criadoEm().toInstant()).isEqualTo(original.categoria().criadoEm().toInstant());
		assertThat(back.categoria().atualizadoEm().toInstant()).isEqualTo(original.categoria().atualizadoEm().toInstant());

		CorResponse backCor = back.cores().iterator().next();
		CorResponse origCor = original.cores().iterator().next();
		assertThat(backCor.criadoEm().toInstant()).isEqualTo(origCor.criadoEm().toInstant());
		assertThat(backCor.atualizadoEm().toInstant()).isEqualTo(origCor.atualizadoEm().toInstant());
    }
}
