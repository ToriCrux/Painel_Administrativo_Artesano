package com.sistema.admin.catalogo.cor.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.admin.catalogo.categoria.api.dto.CategoriaRequest;
import com.sistema.admin.catalogo.cor.api.dto.CorRequest;
import com.sistema.admin.catalogo.cor.api.dto.CorResponse;
import com.sistema.admin.catalogo.cor.aplicacao.CorService;
import com.sistema.admin.config.TestSecurityConfig;
import com.sistema.admin.config.jwt.JwtAuthenticationFilter;
import com.sistema.admin.config.jwt.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = CorController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
class CorControllerTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	ObjectMapper om;

	@MockBean
	private CorService corService;

	@MockBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Mock
	private JwtUtil jwtUtil;

	private static CorResponse corResponse(long id, String nome, String hex, boolean ativo) {
		return new CorResponse(
				id, nome, hex, ativo,
				OffsetDateTime.parse("2025-09-25T18:00:00-03:00"),
				OffsetDateTime.parse("2025-09-25T18:30:00-03:00")
		);
	}

	@Test
	@DisplayName("GET /categorias: 200 com página e itens")
	void listar_ok() throws Exception {
		var pageable = PageRequest.of(0, 2, Sort.by("nome").ascending());
		var page = new PageImpl<>(
				List.of(corResponse(1, "Pale Dogwood", "#C5AFA4", true), corResponse(2, "Robin Egg Blue", "#55DDE0", true)),
				pageable, 2);

		when(corService.listar(any(), any(Pageable.class))).thenReturn(page);

		mvc.perform(get("/api/v1/cores")
						.param("page", "0")
						.param("size", "2")
						.param("sort", "nome,asc")
						.with(jwt())
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.content", hasSize(2)))
				.andExpect(jsonPath("$.content[*].nome", containsInAnyOrder("Pale Dogwood", "Robin Egg Blue")))
				.andExpect(jsonPath("$.content[*].hex", containsInAnyOrder("#C5AFA4", "#55DDE0")))
				.andExpect(jsonPath("$.totalElements").value(2));

		ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
		verify(corService).listar(isNull(), captor.capture()); // nome null
		Pageable used = captor.getValue();
		assertThat(used.getPageNumber()).isEqualTo(0);
		assertThat(used.getPageSize()).isEqualTo(2);
	}

	@Test
	@DisplayName("GET /cores?nome=ins: 204 quando página vazia")
	void listar_noContent() throws Exception {
		var pageable = PageRequest.of(0, 10);
		when(corService.listar(eq("ins"), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		mvc.perform(get("/api/v1/cores").param("nome","ins"))
				.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("GET /cores/{id}: 200 quando encontrado")
	void listarPorId_ok() throws Exception {
		when(corService.listarPorId(10L)).thenReturn(corResponse(1, "Pale Dogwood", "#C5AFA4", true));

		mvc.perform(get("/api/v1/cores/{id}", 10))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.nome").value("Pale Dogwood"));
	}

	@Test
	@DisplayName("POST /cores: 201 com corpo")
	void salvar_created() throws Exception {
		var corRequest = new CorRequest("Robin Egg Blue", "#55DDE0", true);
		when(corService.salvar(any())).thenReturn(corResponse(2, "Robin Egg Blue", "#55DDE0", true));

		mvc.perform(post("/api/v1/cores")
						.contentType(MediaType.APPLICATION_JSON)
						.content(om.writeValueAsString(corRequest)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(2))
				.andExpect(jsonPath("$.nome").value("Robin Egg Blue"));
	}

	@Test
	@DisplayName("POST /cores: 400 quando payload inválido (nome em branco)")
	void salvar_badRequest_validacao() throws Exception {
		var reqJson = """
            {"nome":"   ","ativo":true}
        """;
		mvc.perform(post("/api/v1/cores")
						.contentType(MediaType.APPLICATION_JSON)
						.content(reqJson))
				.andExpect(status().isBadRequest());
		verify(corService, never()).salvar(any());
	}

	@Test
	@DisplayName("PUT /cores/{id}: 201 com corpo atualizado")
	void atualizar_created() throws Exception {
		var corRequest = new CorRequest("Robin Egg Blue", "#55DDE0", true);
		when(corService.atualizar(eq(10L), any())).thenReturn(corResponse(2, "Robin Egg Blue", "#55DDE0", false));

		mvc.perform(put("/api/v1/cores/{id}", 10)
						.with(jwt())
						.contentType(MediaType.APPLICATION_JSON)
						.content(om.writeValueAsString(corRequest)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(2))
				.andExpect(jsonPath("$.nome").value("Robin Egg Blue"))
				.andExpect(jsonPath("$.ativo").value(false));
	}

	@Test
	@DisplayName("PUT /cores/{id}/status: 201 desativado")
	void desativar_created() throws Exception {
		when(corService.desativar(7L)).thenReturn(corResponse(2, "Robin Egg Blue", "#55DDE0", false));

		mvc.perform(put("/api/v1/cores/{id}/status", 7))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(2))
				.andExpect(jsonPath("$.ativo").value(false));
	}

	@Test
	@DisplayName("DELETE /cores/{id}: 204")
	void deletar_noContent() throws Exception {
		mvc.perform(delete("/api/v1/cores/{id}", 99))
				.andExpect(status().isNoContent());
		verify(corService).deletar(99L);
	}

}