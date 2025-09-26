package com.sistema.admin.catalogo.categoria.api.dto;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CategoriaResponseTest {

	private static ObjectMapper om() {
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new JavaTimeModule());
		om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		om.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
		return om;
	}

	@Test
	void getters_equals_hashcode_toString() {
		OffsetDateTime criado   = OffsetDateTime.parse("2025-09-25T18:00:00-03:00");
		OffsetDateTime atualizado = OffsetDateTime.parse("2025-09-25T18:30:00-03:00");

		CategoriaResponse c1 = new CategoriaResponse(1L, "Insumos", true, criado, atualizado);
		CategoriaResponse c2 = new CategoriaResponse(1L, "Insumos", true, criado, atualizado);
		CategoriaResponse c3 = new CategoriaResponse(2L, "Equipamentos", false, criado.plusDays(1), atualizado.plusDays(1));

		assertThat(c1.id()).isEqualTo(1L);
		assertThat(c1.nome()).isEqualTo("Insumos");
		assertThat(c1.ativo()).isTrue();
		assertThat(c1.criadoEm()).isEqualTo(criado);
		assertThat(c1.atualizadoEm()).isEqualTo(atualizado);

		assertThat(c1).isEqualTo(c2).hasSameHashCodeAs(c2);
		assertThat(c1).isNotEqualTo(c3);

		assertThat(c1.toString())
				.contains("CategoriaResponse")
				.contains("Insumos");
	}

	@Test
	void jackson_serialization_roundtrip() throws Exception {
		var om = om();
		OffsetDateTime criado   = OffsetDateTime.parse("2025-09-25T18:00:00-03:00");
		OffsetDateTime atualizado = OffsetDateTime.parse("2025-09-25T18:30:00-03:00");

		CategoriaResponse original =
				new CategoriaResponse(10L, "Periféricos", true, criado, atualizado);

		String json = om.writeValueAsString(original);
		assertThat(json)
				.contains("\"id\":10")
				.contains("\"nome\":\"Periféricos\"")
				.contains("\"ativo\":true")
				.contains("\"criadoEm\":\"2025-09-25T18:00:00-03:00\"")
				.contains("\"atualizadoEm\":\"2025-09-25T18:30:00-03:00\"");

		CategoriaResponse back = om.readValue(json, CategoriaResponse.class);
		assertThat(back).isEqualTo(original);
	}
}