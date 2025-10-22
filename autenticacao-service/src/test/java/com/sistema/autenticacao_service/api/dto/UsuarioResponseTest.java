package com.sistema.autenticacao_service.api.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.autenticacao_service.dominio.Usuario;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioResponseTest {

	@Test
	void getters_equals_hashcode_toString() {
		String roles1 = Usuario.UsuarioRole.USUARIO.name();

		UsuarioResponse u1 = new UsuarioResponse(1L, "Ana", "ana@example.com", true, roles1);
		UsuarioResponse u2 = new UsuarioResponse(1L, "Ana", "ana@example.com", true, roles1);
		UsuarioResponse u3 = new UsuarioResponse(2L, "Bruno", "bruno@example.com", true, Usuario.UsuarioRole.USUARIO.name());

		assertThat(u1.id()).isEqualTo(1L);
		assertThat(u1.nome()).isEqualTo("Ana");
		assertThat(u1.email()).isEqualTo("ana@example.com");
		assertThat(u1.ativo()).isTrue();

		assertThat(u1).isEqualTo(u2).hasSameHashCodeAs(u2);
		assertThat(u1).isNotEqualTo(u3);

		assertThat(u1.toString())
				.contains("Ana")
				.contains("ana@example.com")
				.contains("USUARIO");
	}

	@Test
	void jackson_serialization_roundtrip() throws Exception {
		ObjectMapper om = new ObjectMapper();
		var role = Usuario.UsuarioRole.USUARIO.name();
		UsuarioResponse original = new UsuarioResponse(10L, "Camila", "camila@example.com", true, role);

		String json = om.writeValueAsString(original);
		assertThat(json)
				.contains("\"id\":10")
				.contains("\"nome\":\"Camila\"")
				.contains("\"email\":\"camila@example.com\"")
				.contains("\"ativo\":true")
				.contains("USUARIO");

		UsuarioResponse back = om.readValue(json, UsuarioResponse.class);
		assertThat(back).isEqualTo(original);
	}
}