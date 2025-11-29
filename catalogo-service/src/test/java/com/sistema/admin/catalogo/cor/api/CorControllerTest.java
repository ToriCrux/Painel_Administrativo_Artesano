package com.sistema.admin.catalogo.cor.api;

import com.sistema.admin.catalogo.cor.api.dto.CorRequest;
import com.sistema.admin.catalogo.cor.api.dto.CorResponse;
import com.sistema.admin.catalogo.cor.aplicacao.CorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
class CorControllerTest {

	@Mock
	private CorService corService;

	@InjectMocks
	private CorController corController;

	private static CorResponse corResponse(long id, String nome, String hex, boolean ativo) {
		return new CorResponse(
				id, nome, hex, ativo,
				OffsetDateTime.parse("2025-09-25T18:00:00-03:00"),
				OffsetDateTime.parse("2025-09-25T18:30:00-03:00")
		);
	}

	@Test
	@DisplayName("GET /cores: 200 com página e itens")
	void listar_ok() {
		var pageable = PageRequest.of(0, 2, Sort.by("nome").ascending());
		var page = new PageImpl<>(
				List.of(
						corResponse(1, "Pale Dogwood", "#C5AFA4", true),
						corResponse(2, "Robin Egg Blue", "#55DDE0", true)
				),
				pageable, 2
		);

		when(corService.listar(isNull(), any(Pageable.class))).thenReturn(page);

		ResponseEntity<?> resp = corController.listarCores(null, pageable);

		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
		@SuppressWarnings("unchecked")
		var body = (org.springframework.data.domain.Page<CorResponse>) resp.getBody();
		assertThat(body).isNotNull();
		assertThat(body.getContent()).hasSize(2);
		assertThat(body.getTotalElements()).isEqualTo(2);

		ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
		verify(corService).listar(isNull(), captor.capture());
		Pageable used = captor.getValue();
		assertThat(used.getPageNumber()).isEqualTo(0);
		assertThat(used.getPageSize()).isEqualTo(2);
		assertThat(used.getSort().getOrderFor("nome")).isNotNull();
	}

	@Test
	@DisplayName("GET /cores?nome=ins: 204 quando página vazia")
	void listar_noContent() {
		var pageable = PageRequest.of(0, 10);
		when(corService.listar(eq("ins"), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		ResponseEntity<?> resp = corController.listarCores("ins", pageable);

		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(resp.getBody()).isNull();
	}

	@Test
	@DisplayName("GET /cores/{id}: 200 quando encontrado")
	void listarPorId_ok() {
		when(corService.listarPorId(10L))
				.thenReturn(corResponse(1, "Pale Dogwood", "#C5AFA4", true));

		ResponseEntity<CorResponse> resp = corController.listarCorPorId(10L);

		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(resp.getBody()).isNotNull();
		assertThat(resp.getBody().id()).isEqualTo(1L);
		assertThat(resp.getBody().nome()).isEqualTo("Pale Dogwood");
	}

	@Test
	@DisplayName("GET /cores/{id}: 404 quando não encontrado")
	void listarPorId_notFound() {
		when(corService.listarPorId(10L)).thenReturn(null);

		ResponseEntity<CorResponse> resp = corController.listarCorPorId(10L);

		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(resp.getBody()).isNull();
	}

	@Test
	@DisplayName("POST /cores: 201 com corpo")
	void salvar_created() {
		var req = new CorRequest("Robin Egg Blue", "#55DDE0", true);
		when(corService.salvar(any())).thenReturn(corResponse(2, "Robin Egg Blue", "#55DDE0", true));

		ResponseEntity<CorResponse> resp = corController.salvarCor(req);

		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(resp.getBody()).isNotNull();
		assertThat(resp.getBody().id()).isEqualTo(2L);
		assertThat(resp.getBody().nome()).isEqualTo("Robin Egg Blue");

		verify(corService).salvar(req);
	}

	@Test
	@DisplayName("PUT /cores/{id}: 201 com corpo atualizado")
	void atualizar_created() {
		var req = new CorRequest("Robin Egg Blue", "#55DDE0", true);
		when(corService.atualizar(eq(10L), any())).thenReturn(corResponse(2, "Robin Egg Blue", "#55DDE0", false));

		ResponseEntity<CorResponse> resp = corController.atualizarCor(10L, req);

		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(resp.getBody()).isNotNull();
		assertThat(resp.getBody().id()).isEqualTo(2L);
		assertThat(resp.getBody().ativo()).isFalse();

		verify(corService).atualizar(10L, req);
	}

	@Test
	@DisplayName("PUT /cores/{id}/status: 201 desativado")
	void desativar_created() {
		when(corService.desativar(7L)).thenReturn(corResponse(7, "Robin Egg Blue", "#55DDE0", false));

		ResponseEntity<CorResponse> resp = corController.desativar(7L);

		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(resp.getBody()).isNotNull();
		assertThat(resp.getBody().ativo()).isFalse();

		verify(corService).desativar(7L);
	}

	@Test
	@DisplayName("DELETE /cores/{id}: 204")
	void deletar_noContent() {
		doNothing().when(corService).deletar(99L);

		ResponseEntity<Void> resp = corController.deletar(99L);

		assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(resp.getBody()).isNull();
		verify(corService).deletar(99L);
	}
}
