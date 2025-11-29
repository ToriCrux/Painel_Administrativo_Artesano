package com.sistema.admin.catalogo.cor.aplicacao;

import com.sistema.admin.catalogo.cor.api.dto.CorRequest;
import com.sistema.admin.catalogo.cor.api.dto.CorResponse;
import com.sistema.admin.catalogo.cor.dominio.Cor;
import com.sistema.admin.catalogo.cor.infra.CorRepository;
import com.sistema.admin.config.exception.ConflictException;
import com.sistema.admin.config.exception.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorServiceTest {

	@Mock
	CorRepository corRepository;

	@InjectMocks
	CorService corService;

	@Captor
	ArgumentCaptor<Cor> corCaptor;

	private Pageable pageable;

	@BeforeEach
	void setUp() {
		pageable = PageRequest.of(0, 10, Sort.by("nome").ascending());
	}

	// ---------- listar ----------

	@Test
	void listar_quandoNomeInformado_deveUsarFindByNomeContainingIgnoreCase_eMapearParaResponse() {
		var c1 = cor(1L, "Azul", "#0000FF", true);
		var c2 = cor(2L, "Azul escuro", "#000099", true);
		when(corRepository.findByNomeContainingIgnoreCase(eq("azul"), eq(pageable)))
				.thenReturn(new PageImpl<>(List.of(c1, c2), pageable, 2));

		Page<CorResponse> page = corService.listar("azul", pageable);

		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent())
				.extracting(CorResponse::id, CorResponse::nome, CorResponse::hex, CorResponse::ativo)
				.containsExactly(
						tuple(1L, "Azul", "#0000FF", true),
						tuple(2L, "Azul escuro", "#000099", true)
				);

		verify(corRepository).findByNomeContainingIgnoreCase("azul", pageable);
		verify(corRepository, never()).findAll(any(Pageable.class));
	}

	@Test
	void listar_quandoNomeNullOuBlank_deveUsarFindAll() {
		when(corRepository.findAll(eq(pageable)))
				.thenReturn(new PageImpl<>(List.of(cor(1L, "Vermelho", "#FF0000", true)), pageable, 1));

		Page<CorResponse> page1 = corService.listar(null, pageable);
		Page<CorResponse> page2 = corService.listar("   ", pageable);

		assertThat(page1.getTotalElements()).isEqualTo(1);
		assertThat(page2.getTotalElements()).isEqualTo(1);

		verify(corRepository, times(2)).findAll(pageable);
		verify(corRepository, never()).findByNomeContainingIgnoreCase(anyString(), any(Pageable.class));
	}

	// ---------- listarPorId ----------

	@Test
	void listarPorId_quandoExiste_deveRetornarResponse() {
		when(corRepository.findById(1L)).thenReturn(Optional.of(cor(1L, "Azul", "#0000FF", true)));

		CorResponse resp = corService.listarPorId(1L);

		assertThat(resp.id()).isEqualTo(1L);
		assertThat(resp.nome()).isEqualTo("Azul");
		assertThat(resp.hex()).isEqualTo("#0000FF");
		assertThat(resp.ativo()).isTrue();
	}

	@Test
	void listarPorId_quandoNaoExiste_deveLancarNotFoundException() {
		when(corRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> corService.listarPorId(99L))
				.isInstanceOf(NotFoundException.class)
				.hasMessageContaining("Cor não encontrada");
	}

	// ---------- salvar ----------

	@Test
	void salvar_quandoNomeJaExiste_deveLancarConflictException_eNaoSalvar() {
		var req = new CorRequest("Azul", "#0000FF", true);
		when(corRepository.findByNomeIgnoreCase("Azul")).thenReturn(Optional.of(cor(10L, "AZUL", "#0000AA", true)));

		assertThatThrownBy(() -> corService.salvar(req))
				.isInstanceOf(ConflictException.class)
				.hasMessageContaining("Cor existente");

		verify(corRepository, never()).save(any(Cor.class));
	}

	@Test
	void salvar_quandoAtivoNull_deveDefaultarTrue_eSalvar() {
		var req = new CorRequest("Branco", "#FFFFFF", null);

		when(corRepository.findByNomeIgnoreCase("Branco")).thenReturn(Optional.empty());
		when(corRepository.save(any(Cor.class))).thenAnswer(inv -> {
			Cor toSave = inv.getArgument(0);
			toSave.setId(1L);
			return toSave;
		});

		CorResponse resp = corService.salvar(req);

		verify(corRepository).save(corCaptor.capture());
		Cor saved = corCaptor.getValue();

		assertThat(saved.getNome()).isEqualTo("Branco");
		assertThat(saved.getHex()).isEqualTo("#FFFFFF");
		assertThat(saved.getAtivo()).isTrue(); // default
		assertThat(resp.id()).isEqualTo(1L);
		assertThat(resp.ativo()).isTrue();
	}

	// ---------- atualizar ----------

	@Test
	void atualizar_quandoNaoExiste_deveLancarEntityNotFoundException() {
		when(corRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> corService.atualizar(1L, new CorRequest("Azul", "#0000FF", true)))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessageContaining("Cor não encontrada");
	}

	@Test
	void atualizar_quandoNomeMudaEJaExisteOutroComMesmoNome_deveLancarConflictException() {
		Cor existente = cor(1L, "Azul", "#0000FF", true);

		when(corRepository.findById(1L)).thenReturn(Optional.of(existente));
		when(corRepository.findByNomeIgnoreCase("Vermelho")).thenReturn(Optional.of(cor(2L, "Vermelho", "#FF0000", true)));

		assertThatThrownBy(() -> corService.atualizar(1L, new CorRequest("Vermelho", "#FF0000", true)))
				.isInstanceOf(ConflictException.class)
				.hasMessageContaining("Cor já existe");

		verify(corRepository, never()).save(any(Cor.class));
	}

	@Test
	void atualizar_quandoNomeIgualIgnorandoCase_naoDeveChecarConflito_eDeveSalvarAlteracoes() {
		Cor existente = cor(1L, "Azul", "#0000FF", true);

		when(corRepository.findById(1L)).thenReturn(Optional.of(existente));
		when(corRepository.save(any(Cor.class))).thenAnswer(inv -> inv.getArgument(0));

		CorResponse resp = corService.atualizar(1L, new CorRequest("aZuL", "#1111FF", false));

		verify(corRepository, never()).findByNomeIgnoreCase(anyString());
		verify(corRepository).save(corCaptor.capture());

		Cor saved = corCaptor.getValue();
		assertThat(saved.getNome()).isEqualTo("aZuL");
		assertThat(saved.getHex()).isEqualTo("#1111FF");
		assertThat(saved.getAtivo()).isFalse();

		assertThat(resp.nome()).isEqualTo("aZuL");
		assertThat(resp.hex()).isEqualTo("#1111FF");
		assertThat(resp.ativo()).isFalse();
	}

	// ---------- desativar ----------

	@Test
	void desativar_quandoExiste_deveSetarAtivoFalse_eSalvar() {
		Cor existente = cor(1L, "Azul", "#0000FF", true);

		when(corRepository.findById(1L)).thenReturn(Optional.of(existente));
		when(corRepository.save(any(Cor.class))).thenAnswer(inv -> inv.getArgument(0));

		CorResponse resp = corService.desativar(1L);

		verify(corRepository).save(corCaptor.capture());
		assertThat(corCaptor.getValue().getAtivo()).isFalse();
		assertThat(resp.ativo()).isFalse();
	}

	@Test
	void desativar_quandoNaoExiste_deveLancarNotFoundException() {
		when(corRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> corService.desativar(1L))
				.isInstanceOf(NotFoundException.class)
				.hasMessageContaining("Cor não encontrada");
	}

	// ---------- deletar ----------

	@Test
	void deletar_quandoExiste_deveDeletarPorId() {
		when(corRepository.findById(1L)).thenReturn(Optional.of(cor(1L, "Azul", "#0000FF", true)));

		corService.deletar(1L);

		verify(corRepository).deleteById(1L);
	}

	@Test
	void deletar_quandoNaoExiste_deveLancarNotFoundException_eNaoDeletar() {
		when(corRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> corService.deletar(1L))
				.isInstanceOf(NotFoundException.class)
				.hasMessageContaining("Cor não encontrada");

		verify(corRepository, never()).deleteById(anyLong());
	}

	// ---------- helper ----------
	private Cor cor(Long id, String nome, String hex, boolean ativo) {
		// Ajuste aqui conforme sua entidade (builder, construtor, setters)
		Cor c = Cor.builder()
				.id(id)
				.nome(nome)
				.hex(hex)
				.ativo(ativo)
				.build();
		return c;
	}
}