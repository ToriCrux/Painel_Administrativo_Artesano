package com.sistema.admin.catalogo.categoria.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.admin.catalogo.categoria.api.dto.CategoriaRequest;
import com.sistema.admin.catalogo.categoria.api.dto.CategoriaResponse;
import com.sistema.admin.catalogo.categoria.aplicacao.CategoriaService;
import com.sistema.admin.config.TestSecurityConfig;
import com.sistema.admin.config.jwt.JwtAuthenticationFilter;
import com.sistema.admin.config.jwt.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = CategoriaController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
class CategoriaControllerTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	ObjectMapper om;

	@MockBean
	private CategoriaService categoriaService;

	@MockBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Mock
	private JwtUtil jwtUtil;

	private static CategoriaResponse dto(long id, String nome, boolean ativo) {
		return new CategoriaResponse(
				id, nome, ativo,
				OffsetDateTime.parse("2025-09-25T18:00:00-03:00"),
				OffsetDateTime.parse("2025-09-25T18:30:00-03:00")
		);
	}

	// ---------- GET /api/v1/categorias ----------
	@Test
	@DisplayName("GET /categorias: 200 com página e itens")
	void listar_ok() throws Exception {
		var pageable = PageRequest.of(0, 2, Sort.by("nome").ascending());
		var page = new PageImpl<>(List.of(dto(1, "Insumos", true), dto(2, "Periféricos", true)), pageable, 2);

//		when(categoriaService.listar(isNull(), any(Pageable.class))).thenReturn(page);

		mvc.perform(get("/api/v1/categorias")
						.with(jwt()))
//				.param("page","0").param("size","2").param("sort","nome,asc"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.content", hasSize(2)))
				.andExpect(jsonPath("$.content[*].nome", containsInAnyOrder("Insumos","Periféricos")))
				.andExpect(jsonPath("$.totalElements").value(2));

		// captura do Pageable
		ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
		verify(categoriaService).listar(eq(null), captor.capture());
		Pageable used = captor.getValue();
		assertThat(used.getPageNumber()).isEqualTo(0);
		assertThat(used.getPageSize()).isEqualTo(2);
	}

//	@Test
//	@DisplayName("GET /categorias?nome=ins: 204 quando página vazia")
//	void listar_noContent() throws Exception {
//		var pageable = PageRequest.of(0, 10);
//		when(categoriaService.listar(eq("ins"), any(Pageable.class)))
//				.thenReturn(new PageImpl<>(List.of(), pageable, 0));
//
//		mvc.perform(get("/api/v1/categorias").param("nome","ins"))
//				.andExpect(status().isNoContent());
//	}

	// ---------- GET /api/v1/categorias/{id} ----------
	@Test
	@DisplayName("GET /categorias/{id}: 200 quando encontrado")
	void listarPorId_ok() throws Exception {
		when(categoriaService.listarPorId(10L)).thenReturn(dto(10, "Insumos", true));

		mvc.perform(get("/api/v1/categorias/{id}", 10))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(10))
				.andExpect(jsonPath("$.nome").value("Insumos"));
	}

	// ---------- POST /api/v1/categorias ----------
	@Test
	@DisplayName("POST /categorias: 201 com corpo")
	void salvar_created() throws Exception {
		var req = new CategoriaRequest("Periféricos", true);
		when(categoriaService.salvar(any())).thenReturn(dto(50, "Periféricos", true));

		mvc.perform(post("/api/v1/categorias")
						.contentType(MediaType.APPLICATION_JSON)
						.content(om.writeValueAsString(req)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(50))
				.andExpect(jsonPath("$.nome").value("Periféricos"));
	}

	@Test
	@DisplayName("POST /categorias: 400 quando payload inválido (nome em branco)")
	void salvar_badRequest_validacao() throws Exception {
		var reqJson = """
            {"nome":"   ","ativo":true}
        """;
		mvc.perform(post("/api/v1/categorias")
						.contentType(MediaType.APPLICATION_JSON)
						.content(reqJson))
				.andExpect(status().isBadRequest());
		verify(categoriaService, never()).salvar(any());
	}

	// ---------- PUT /api/v1/categorias/{id} ----------
	@Test
	@DisplayName("PUT /categorias/{id}: 201 com corpo atualizado")
	void atualizar_created() throws Exception {
		var req = new CategoriaRequest("Equipamentos", false);
		when(categoriaService.atualizar(eq(10L), any())).thenReturn(dto(10, "Equipamentos", false));

		mvc.perform(put("/api/v1/categorias/{id}", 10)
						.contentType(MediaType.APPLICATION_JSON)
						.content(om.writeValueAsString(req)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(10))
				.andExpect(jsonPath("$.nome").value("Equipamentos"))
				.andExpect(jsonPath("$.ativo").value(false));
	}

	// ---------- PUT /api/v1/categorias/{id}/status ----------
	@Test
	@DisplayName("PUT /categorias/{id}/status: 201 desativado")
	void desativar_created() throws Exception {
		when(categoriaService.desativar(7L)).thenReturn(dto(7, "Insumos", false));

		mvc.perform(put("/api/v1/categorias/{id}/status", 7))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(7))
				.andExpect(jsonPath("$.ativo").value(false));
	}

	// ---------- DELETE /api/v1/categorias/{id} ----------
	@Test
	@DisplayName("DELETE /categorias/{id}: 204")
	void deletar_noContent() throws Exception {
		mvc.perform(delete("/api/v1/categorias/{id}", 99))
				.andExpect(status().isNoContent());
		verify(categoriaService).deletar(99L);
	}
}