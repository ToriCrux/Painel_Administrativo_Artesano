package com.sistema.admin.catalogo.categoria.api;

import com.sistema.catalogoservice.catalogo.categoria.api.CategoriaController;
import com.sistema.catalogoservice.catalogo.categoria.api.dto.*;
import com.sistema.catalogoservice.catalogo.categoria.aplicacao.CategoriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

	// 🔹 Helpers de DTOs
	private static ItemCategoriaResponse item(long id, String nome, boolean ativo) {
		return new ItemCategoriaResponse(
				id, nome, ativo,
				OffsetDateTime.parse("2025-09-25T18:00:00-03:00"),
				OffsetDateTime.parse("2025-09-25T18:30:00-03:00")
		);
	}

	private static SubcategoriaResponse sub(long id, String nome, boolean ativo) {
		return new SubcategoriaResponse(
				id, nome, ativo,
				OffsetDateTime.parse("2025-09-25T18:00:00-03:00"),
				OffsetDateTime.parse("2025-09-25T18:30:00-03:00"),
				List.of(item(100, "Mouse", true), item(101, "Teclado", true))
		);
	}

	private static CategoriaResponse cat(long id, String nome, boolean ativo) {
		return new CategoriaResponse(
				id, nome, ativo,
				OffsetDateTime.parse("2025-09-25T18:00:00-03:00"),
				OffsetDateTime.parse("2025-09-25T18:30:00-03:00"),
				List.of(sub(10, "Periféricos", true))
		);
	}

	// 🧪 TESTES

	@Test
	@DisplayName("GET /categorias/{id}: 200 quando encontrado com subcategorias e itens")
	void listarPorId_ok() {
		when(categoriaService.listarPorId(1L)).thenReturn(cat(1, "Insumos", true));

		ResponseEntity<CategoriaResponse> resp = categoriaController.buscarPorId(1L);

		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(resp.getBody()).isNotNull();
		assertThat(resp.getBody().nome()).isEqualTo("Insumos");
		assertThat(resp.getBody().subcategorias()).hasSize(1);
		assertThat(resp.getBody().subcategorias().get(0).itens()).hasSize(2);

		verify(categoriaService).listarPorId(1L);
	}

	@Test
	@DisplayName("GET /categorias/{id}: 404 quando não encontrado")
	void listarPorId_notFound() {
		when(categoriaService.listarPorId(10L)).thenThrow(new RuntimeException("Categoria não encontrada"));

		try {
			categoriaController.buscarPorId(10L);
		} catch (RuntimeException e) {
			assertThat(e.getMessage()).contains("Categoria não encontrada");
		}

		verify(categoriaService).listarPorId(10L);
	}

	@Test
	@DisplayName("POST /categorias: 201 com subcategorias e itens criados")
	void salvar_created() {
		var req = new CategoriaRequest(
				"Equipamentos",
				true,
				List.of(new SubcategoriaRequest(
						"Periféricos", true,
						List.of(new ItemCategoriaRequest("Mouse", true),
								new ItemCategoriaRequest("Teclado", true))
				))
		);

		when(categoriaService.salvar(any())).thenReturn(cat(99, "Equipamentos", true));

		ResponseEntity<CategoriaResponse> resp = categoriaController.criar(req);

		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(resp.getBody()).isNotNull();
		assertThat(resp.getBody().id()).isEqualTo(99L);
		assertThat(resp.getBody().subcategorias()).hasSize(1);

		ArgumentCaptor<CategoriaRequest> captor = ArgumentCaptor.forClass(CategoriaRequest.class);
		verify(categoriaService).salvar(captor.capture());
		CategoriaRequest used = captor.getValue();

		assertThat(used.nome()).isEqualTo("Equipamentos");
		assertThat(used.subcategorias()).hasSize(1);
		assertThat(used.subcategorias().get(0).itens()).hasSize(2);
	}

	@Test
	@DisplayName("PUT /categorias/{id}: 200 atualização completa com subcategorias e itens")
	void atualizar_ok() {
		var req = new CategoriaRequest(
				"Eletrônicos",
				true,
				List.of(new SubcategoriaRequest("Consoles", true,
						List.of(new ItemCategoriaRequest("Playstation", true))))
		);

		when(categoriaService.atualizar(eq(5L), any())).thenReturn(cat(5, "Eletrônicos", true));

		ResponseEntity<CategoriaResponse> resp = categoriaController.atualizar(5L, req);

		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(resp.getBody()).isNotNull();
		assertThat(resp.getBody().nome()).isEqualTo("Eletrônicos");

		verify(categoriaService).atualizar(eq(5L), any());
	}

	@Test
	@DisplayName("DELETE /categorias/{id}: 204 ao deletar com sucesso")
	void deletar_ok() {
		doNothing().when(categoriaService).deletar(77L);

		ResponseEntity<Void> resp = categoriaController.deletar(77L);

		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		verify(categoriaService).deletar(77L);
	}
}
