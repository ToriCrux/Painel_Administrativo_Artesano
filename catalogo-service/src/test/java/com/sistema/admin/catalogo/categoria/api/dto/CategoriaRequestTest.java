package com.sistema.admin.catalogo.categoria.api.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.*;
import org.junit.jupiter.api.*;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CategoriaRequestTest {

	private static Validator validator;

	@BeforeAll
	static void setup() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	@Test
	void valido() {
		var dto = new CategoriaRequest("Insumos", true);
		assertThat(validator.validate(dto)).isEmpty();
	}

	@Test
	void nome_null_ou_blank_viola_NotBlank() {
		for (String invalido : new String[]{null, "", "   "}) {
			var dto = new CategoriaRequest(invalido, false);
			Set<ConstraintViolation<CategoriaRequest>> v = validator.validate(dto);

			assertThat(v).isNotEmpty();
			assertThat(v.stream().anyMatch(cv -> cv.getPropertyPath().toString().equals("nome"))).isTrue();
		}
	}

	@Test
	void jackson_roundtrip() throws Exception {
		ObjectMapper om = new ObjectMapper();
		var original = new CategoriaRequest("Equipamentos", true);

		String json = om.writeValueAsString(original);
		assertThat(json).contains("\"nome\":\"Equipamentos\"", "\"ativo\":true");

		var back = om.readValue(json, CategoriaRequest.class);
		assertThat(back).isEqualTo(original);
	}
}