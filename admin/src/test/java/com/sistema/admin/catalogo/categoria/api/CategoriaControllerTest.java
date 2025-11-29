package com.sistema.admin.catalogo.categoria.api;

import com.sistema.admin.catalogo.categoria.api.dto.CategoriaRequest;
import com.sistema.admin.catalogo.categoria.api.dto.CategoriaResponse;
import com.sistema.admin.catalogo.categoria.aplicacao.CategoriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

	@Mock
	private CategoriaService categoriaService;

	@InjectMocks
	private CategoriaController categoriaController;

	private static CategoriaResponse dto(long id, String nome, boolean ativo) {
		return new CategoriaResponse(
				id, nome, ativo,
				OffsetDateTime.parse("2025-09-25T18:00:00-03:00"),
				OffsetDateTime.parse("2025-09-25T18:30:00-03:00")
		);
	}

	@Test
	@DisplayName("GET /categorias: 200 com página e itens")
	void listar_ok() {
		var pageable = PageRequest.of(0, 2, Sort.by("nome").ascending());
		var page = new PageImpl<>(
				List.of(dto(1, "Insumos", true), dto(2, "Periféricos", true)),
				pageable, 2
		);

		when(categoriaService.listar(isNull(), any(Pageable.class))).thenReturn(page);

		ResponseEntity<?> resp = categoriaController.listarCategorias(null, pageable);

		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

		@SuppressWarnings("unchecked")
		Page<CategoriaResponse> body = (Page<CategoriaResponse>) resp.getBody();

		assertThat(body).isNotNull();
		assertThat(body.getContent()).hasSize(2);
		assertThat(body.getTotalElements()).isEqualTo(2);

		ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
		verify(categoriaService).listar(isNull(), captor.capture());
		Pageable used = captor.getValue();
		assertThat(used.getPageNumber()).isEqualTo(0);
		assertThat(used.getPageSize()).isEqualTo(2);
		assertThat(used.getSort().getOrderFor("nome")).isNotNull();
	}

	@Test
	@DisplayName("GET /categorias?nome=ins: 204 quando página vazia")
	void listar_noContent() {
		var pageable = PageRequest.of(0, 10);
		when(categoriaService.listar(eq("ins"), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		ResponseEntity<?> resp = categoriaController.listarCategorias("ins", pageable);

		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(resp.getBody()).isNull();
	}

	@Test
	@DisplayName("GET /categorias/{id}: 200 quando encontrado")
	void listarPorId_ok() {
		when(categoriaService.listarPorId(10L)).thenReturn(dto(10, "Insumos", true));

		ResponseEntity<CategoriaResponse> resp = categoriaController.listarCategoriaPorId(10L);

		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(resp.getBody()).isNotNull();
		assertThat(resp.getBody().id()).isEqualTo(10L);
		assertThat(resp.getBody().nome()).isEqualTo("Insumos");
	}

	@Test
	@DisplayName("GET /categorias/{id}: 404 quando não encontrado")
	void listarPorId_notFound() {
		when(categoriaService.listarPorId(10L)).thenReturn(null);

		ResponseEntity<CategoriaResponse> resp = categoriaController.listarCategoriaPorId(10L);

		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(resp.getBody()).isNull();
	}

	@Test
	@DisplayName("POST /categorias: 201 com corpo")
	void salvar_created() {
		var req = new CategoriaRequest("Periféricos", true);
		when(categoriaService.salvar(any())).thenReturn(dto(50, "Periféricos", true));

		ResponseEntity<CategoriaResponse> resp = categoriaController.salvarCategoria(req);

		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(resp.getBody()).isNotNull();
		assertThat(resp.getBody().id()).isEqualTo(50L);
		assertThat(resp.getBody().nome()).isEqualTo("Periféricos");

		verify(categoriaService).salvar(req);
	}

	@Test
	@DisplayName("PUT /categorias/{id}: 201 com corpo atualizado")
	void atualizar_created() {
		var req = new CategoriaRequest("Equipamentos", false);
		when(categoriaService.atualizar(eq(10L), any())).thenReturn(dto(10, "Equipamentos", false));

		ResponseEntity<CategoriaResponse> resp = categoriaController.atualizarCategoria(10L, req);

		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(resp.getBody()).isNotNull();
		assertThat(resp.getBody().id()).isEqualTo(10L);
		assertThat(resp.getBody().nome()).isEqualTo("Equipamentos");
		assertThat(resp.getBody().ativo()).isFalse();

		verify(categoriaService).atualizar(10L, req);
	}

	@Test
	@DisplayName("PUT /categorias/{id}/status: 201 desativado")
	void desativar_created() {
		when(categoriaService.desativar(7L)).thenReturn(dto(7, "Insumos", false));

		ResponseEntity<CategoriaResponse> resp = categoriaController.desativar(7L);

		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(resp.getBody()).isNotNull();
		assertThat(resp.getBody().id()).isEqualTo(7L);
		assertThat(resp.getBody().ativo()).isFalse();

		verify(categoriaService).desativar(7L);
	}

	@Test
	@DisplayName("DELETE /categorias/{id}: 204")
	void deletar_noContent() {
		doNothing().when(categoriaService).deletar(99L);

		ResponseEntity<Void> resp = categoriaController.deletarCategoria(99L);

		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(resp.getBody()).isNull();
		verify(categoriaService).deletar(99L);
	}
}
