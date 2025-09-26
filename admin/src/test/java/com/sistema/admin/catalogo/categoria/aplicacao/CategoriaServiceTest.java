package com.sistema.admin.catalogo.categoria.aplicacao;

import com.sistema.admin.catalogo.categoria.api.dto.CategoriaRequest;
import com.sistema.admin.catalogo.categoria.api.dto.CategoriaResponse;
import com.sistema.admin.catalogo.categoria.dominio.Categoria;
import com.sistema.admin.catalogo.categoria.infra.CategoriaRepository;
import com.sistema.admin.config.exception.ConflictException;
import com.sistema.admin.config.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

	@Mock
	CategoriaRepository categoriaRepository;

	@InjectMocks
	CategoriaService service;

	// ---------- listar ----------
	@Test
	@DisplayName("listar: sem filtro -> findAll(pageable)")
	void listar_semFiltro() {
		Pageable pageable = PageRequest.of(0, 2, Sort.by("nome"));
		Categoria c1 = cat(1L, "Insumos", true);
		Categoria c2 = cat(2L, "Equipamentos", true);
		when(categoriaRepository.findAll(pageable))
				.thenReturn(new PageImpl<>(List.of(c1, c2), pageable, 2));

		Page<CategoriaResponse> page = service.listar(null, pageable);

		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent()).extracting(CategoriaResponse::nome)
				.containsExactly("Insumos", "Equipamentos");
		verify(categoriaRepository).findAll(pageable);
		verifyNoMoreInteractions(categoriaRepository);
	}

	@Test
	@DisplayName("listar: com filtro nome -> findByNomeContainingIgnoreCase")
	void listar_comFiltro() {
		Pageable pageable = PageRequest.of(1, 1);
		when(categoriaRepository.findByNomeContainingIgnoreCase("ins", pageable))
				.thenReturn(new PageImpl<>(List.of(cat(1L, "Insumos", true)), pageable, 1));

		Page<CategoriaResponse> page = service.listar("ins", pageable);

		assertThat(page).hasSize(1);
		assertThat(page.getContent().get(0).nome()).isEqualTo("Insumos");
		verify(categoriaRepository).findByNomeContainingIgnoreCase("ins", pageable);
		verifyNoMoreInteractions(categoriaRepository);
	}

	// ---------- listarPorId ----------
	@Test
	@DisplayName("listarPorId: encontrado -> retorna DTO")
	void listarPorId_ok() {
		when(categoriaRepository.findById(10L))
				.thenReturn(Optional.of(cat(10L, "Insumos", true)));

		CategoriaResponse dto = service.listarPorId(10L);

		assertThat(dto.id()).isEqualTo(10L);
		assertThat(dto.nome()).isEqualTo("Insumos");
		verify(categoriaRepository).findById(10L);
	}

	@Test
	@DisplayName("listarPorId: não encontrado -> NotFoundException")
	void listarPorId_notFound() {
		when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> service.listarPorId(99L))
				.isInstanceOf(NotFoundException.class)
				.hasMessageContaining("Categoria não encontrada");
		verify(categoriaRepository).findById(99L);
	}

	// ---------- salvar ----------
	@Test
	@DisplayName("salvar: ok quando nome único")
	void salvar_ok() {
		var req = new CategoriaRequest("Periféricos", true);
		when(categoriaRepository.findByNomeIgnoreCase("Periféricos"))
				.thenReturn(Optional.empty());

		ArgumentCaptor<Categoria> captor = ArgumentCaptor.forClass(Categoria.class);
		Categoria salvo = cat(50L, "Periféricos", true);
		when(categoriaRepository.save(any(Categoria.class))).thenReturn(salvo);

		CategoriaResponse dto = service.salvar(req);

		verify(categoriaRepository).findByNomeIgnoreCase("Periféricos");
		verify(categoriaRepository).save(captor.capture());
		Categoria toSave = captor.getValue();
		assertThat(toSave.getId()).isNull();
		assertThat(toSave.getNome()).isEqualTo("Periféricos");
		assertThat(toSave.getAtivo()).isTrue();

		assertThat(dto.id()).isEqualTo(50L);
		assertThat(dto.nome()).isEqualTo("Periféricos");
	}

	@Test
	@DisplayName("salvar: conflito quando nome já existe")
	void salvar_conflito() {
		var req = new CategoriaRequest("Insumos", true);
		when(categoriaRepository.findByNomeIgnoreCase("Insumos"))
				.thenReturn(Optional.of(cat(1L, "Insumos", true)));

		assertThatThrownBy(() -> service.salvar(req))
				.isInstanceOf(ConflictException.class)
				.hasMessageContaining("Categoria já existe");

		verify(categoriaRepository).findByNomeIgnoreCase("Insumos");
		verify(categoriaRepository, never()).save(any());
	}

	// ---------- atualizar ----------
	@Test
	@DisplayName("atualizar: ok quando existe e nome permanece único")
	void atualizar_ok() {
		var req = new CategoriaRequest("Equipamentos", false);

		Categoria existente = cat(10L, "Insumos", true);
		when(categoriaRepository.findById(10L)).thenReturn(Optional.of(existente));
		when(categoriaRepository.findByNomeIgnoreCase("Equipamentos"))
				.thenReturn(Optional.empty());

		Categoria salvo = cat(10L, "Equipamentos", false);
		when(categoriaRepository.save(any(Categoria.class))).thenReturn(salvo);

		CategoriaResponse dto = service.atualizar(10L, req);

		assertThat(dto.id()).isEqualTo(10L);
		assertThat(dto.nome()).isEqualTo("Equipamentos");
		assertThat(dto.ativo()).isFalse();

		verify(categoriaRepository).findById(10L);
		verify(categoriaRepository).findByNomeIgnoreCase("Equipamentos");
		verify(categoriaRepository).save(argThat(c ->
				c.getId().equals(10L) && c.getNome().equals("Equipamentos") && !c.getAtivo()));
	}

	@Test
	@DisplayName("atualizar: não encontrado -> NotFoundException")
	void atualizar_notFound() {
		when(categoriaRepository.findById(77L)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> service.atualizar(77L, new CategoriaRequest("X", true)))
				.isInstanceOf(NotFoundException.class);
		verify(categoriaRepository).findById(77L);
	}

	@Test
	@DisplayName("atualizar: conflito quando novo nome já existe em outra categoria")
	void atualizar_conflito_quandoNovoNomeJaExiste() {
		Categoria existente = cat(10L, "Insumos", true);
		when(categoriaRepository.findById(10L)).thenReturn(Optional.of(existente));
		// novo nome diferente -> consulta duplicidade
		when(categoriaRepository.findByNomeIgnoreCase("Periféricos"))
				.thenReturn(Optional.of(cat(99L, "Periféricos", true)));

		assertThatThrownBy(() -> service.atualizar(10L, new CategoriaRequest("Periféricos", true)))
				.isInstanceOf(ConflictException.class)
				.hasMessageContaining("Categoria já existe");

		verify(categoriaRepository).findById(10L);
		verify(categoriaRepository).findByNomeIgnoreCase("Periféricos");
		verify(categoriaRepository, never()).save(any());
	}

	// ---------- desativar (soft delete) ----------
	@Test
	@DisplayName("desativar: ok -> ativo=false e persiste")
	void desativar_ok() {
		Categoria existente = cat(5L, "Insumos", true);
		when(categoriaRepository.findById(5L)).thenReturn(Optional.of(existente));
		when(categoriaRepository.save(any(Categoria.class)))
				.thenAnswer(inv -> inv.getArgument(0, Categoria.class)); // eco

		CategoriaResponse dto = service.desativar(5L);

		assertThat(dto.id()).isEqualTo(5L);
		assertThat(dto.ativo()).isFalse();

		verify(categoriaRepository).findById(5L);
		verify(categoriaRepository).save(argThat(c -> c.getId().equals(5L) && !c.getAtivo()));
	}

	@Test
	@DisplayName("desativar: não encontrado -> NotFoundException")
	void desativar_notFound() {
		when(categoriaRepository.findById(5L)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> service.desativar(5L))
				.isInstanceOf(NotFoundException.class);
		verify(categoriaRepository).findById(5L);
	}

	// ---------- deletar (hard) ----------
	@Test
	@DisplayName("deletar: ok quando existe")
	void deletar_ok() {
		Categoria existente = cat(3L, "X", true);
		when(categoriaRepository.findById(3L)).thenReturn(Optional.of(existente));

		service.deletar(3L);

		verify(categoriaRepository).findById(3L);
		verify(categoriaRepository).delete(existente);
	}

	@Test
	@DisplayName("deletar: não encontrado -> NotFoundException")
	void deletar_notFound() {
		when(categoriaRepository.findById(3L)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> service.deletar(3L))
				.isInstanceOf(NotFoundException.class);
		verify(categoriaRepository).findById(3L);
		verify(categoriaRepository, never()).delete(any());
	}

	// ---------- helper ----------
	private static Categoria cat(Long id, String nome, boolean ativo) {
		Categoria c = new Categoria();
		c.setId(id);
		c.setNome(nome);
		c.setAtivo(ativo);
		c.setCriadoEm(OffsetDateTime.parse("2025-09-25T18:00:00-03:00"));
		c.setAtualizadoEm(OffsetDateTime.parse("2025-09-25T18:30:00-03:00"));
		return c;
	}
}