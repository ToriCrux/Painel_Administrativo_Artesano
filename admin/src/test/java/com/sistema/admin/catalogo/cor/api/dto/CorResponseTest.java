package com.sistema.admin.catalogo.cor.api.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CorResponseTest {

    @Test
    @DisplayName("CorResponse deve serializar e desserializar preservando os dados")
    void shouldSerializeAndDeserialize() throws Exception {
		ObjectMapper om = new ObjectMapper()
				.findAndRegisterModules()
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        OffsetDateTime criado = OffsetDateTime.parse("2025-09-25T18:00:00-03:00");
        OffsetDateTime atualizado = OffsetDateTime.parse("2025-09-25T18:30:00-03:00");

        CorResponse original = new CorResponse(
                1L,
                "Branco",
                "#FFFFFF",
                true,
                criado,
                atualizado
        );

        String json = om.writeValueAsString(original);

        assertThat(json)
                .contains("\"id\":1")
                .contains("\"nome\":\"Branco\"")
                .contains("\"hex\":\"#FFFFFF\"")
                .contains("\"ativo\":true")
                .contains("\"criadoEm\":\"2025-09-25T18:00:00-03:00\"")
                .contains("\"atualizadoEm\":\"2025-09-25T18:30:00-03:00\"");

        CorResponse back = om.readValue(json, CorResponse.class);
		assertThat(back.criadoEm().toInstant()).isEqualTo(original.criadoEm().toInstant());
		assertThat(back.atualizadoEm().toInstant()).isEqualTo(original.atualizadoEm().toInstant());
		assertThat(back.id()).isEqualTo(original.id());
		assertThat(back.nome()).isEqualTo(original.nome());
		assertThat(back.hex()).isEqualTo(original.hex());
		assertThat(back.ativo()).isEqualTo(original.ativo());
    }
}