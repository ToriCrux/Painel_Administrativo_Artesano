package com.sistema.admin.auth.api.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenResponseTest {

	@Test
	void getters_equals_hashcode_toString() {
		TokenResponse t1 = new TokenResponse("abc123");
		TokenResponse t2 = new TokenResponse("abc123");
		TokenResponse t3 = new TokenResponse("xyz");

		assertThat(t1.token()).isEqualTo("abc123");

		assertThat(t1).isEqualTo(t2).hasSameHashCodeAs(t2);
		assertThat(t1).isNotEqualTo(t3);

		assertThat(t1.toString()).contains("abc123");
	}

	@Test
	void jackson_serialization_roundtrip() throws Exception {
		ObjectMapper om = new ObjectMapper();
		TokenResponse original = new TokenResponse("jwt-token");

		String json = om.writeValueAsString(original);
		assertThat(json).isEqualTo("{\"token\":\"jwt-token\"}");

		TokenResponse back = om.readValue(json, TokenResponse.class);
		assertThat(back).isEqualTo(original);
	}
}