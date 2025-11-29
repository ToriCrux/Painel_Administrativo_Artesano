package com.sistema.admin.catalogo.produto.aplicacao;

import com.sistema.admin.catalogo.categoria.api.dto.CategoriaResponse;
import com.sistema.admin.catalogo.categoria.dominio.Categoria;
import com.sistema.admin.catalogo.categoria.infra.CategoriaRepository;
import com.sistema.admin.catalogo.cor.api.dto.CorResponse;
import com.sistema.admin.catalogo.cor.dominio.Cor;
import com.sistema.admin.catalogo.cor.infra.CorRepository;
import com.sistema.admin.catalogo.produto.api.dto.ProdutoRequest;
import com.sistema.admin.catalogo.produto.api.dto.ProdutoResponse;
import com.sistema.admin.catalogo.produto.dominio.Produto;
import com.sistema.admin.catalogo.produto.infra.ProdutoRepository;
import com.sistema.admin.catalogo.produtoimagem.dominio.ProdutoImagem; // ajuste se necessário
import com.sistema.admin.catalogo.produtoimagem.infra.ProdutoImagemRepository;
import com.sistema.admin.config.exception.ConflictException;
import com.sistema.admin.config.exception.NotFoundException;
import com.sistema.admin.mensageria.RabbitMQConfig;
import com.sistema.admin.mensageria.evento.ProdutoCriadoEvent;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

	@Mock ProdutoRepository produtoRepository;
	@Mock CategoriaRepository categoriaRepository;
	@Mock CorRepository corRepository;
	@Mock ProdutoImagemRepository produtoImagemRepository;
	@Mock RabbitTemplate rabbitTemplate;

	@InjectMocks ProdutoService produtoService;

	@Captor ArgumentCaptor<Produto> produtoCaptor;
	@Captor ArgumentCaptor<Object> eventoCaptor;

	private Pageable pageable;

	@BeforeEach
	void setup() {
		pageable = PageRequest.of(0, 10, Sort.by("nome").ascending());
	}

	// ---------------- listar ----------------

	@Test
	void listar_quandoNomeInformado_deveUsarFindByNomeContainingIgnoreCase() {
		var categoria = categoria(10L, "Camisas", true);
		var p1 = produto(1L, "COD1", "Camisa A", categoria, Set.of(cor(1L, "Azul")), true);
		var p2 = produto(2L, "COD2", "Camisa B", categoria, Set.of(cor(2L, "Vermelho")), true);

		when(produtoRepository.findByNomeContainingIgnoreCase(eq("camisa"), eq(pageable)))
				.thenReturn(new PageImpl<>(List.of(p1, p2), pageable, 2));
		// principalUrl: null por padrão
		when(produtoImagemRepository.findFirstByProdutoIdAndPrincipalTrue(anyLong()))
				.thenReturn(Optional.empty());

		Page<ProdutoResponse> page = produtoService.listar("camisa", pageable);

		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getContent()).extracting(ProdutoResponse::id, ProdutoResponse::codigo, ProdutoResponse::nome)
				.containsExactly(
						tuple(1L, "COD1", "Camisa A"),
						tuple(2L, "COD2", "Camisa B")
				);

		verify(produtoRepository).findByNomeContainingIgnoreCase("camisa", pageable);
		verify(produtoRepository, never()).findAll(any(Pageable.class));
	}

	@Test
	void listar_quandoNomeNullOuBlank_deveUsarFindAll() {
		var categoria = categoria(10L, "Camisas", true);
		var p1 = produto(1L, "COD1", "Camisa A", categoria, Set.of(cor(1L, "Azul")), true);

		when(produtoRepository.findAll(eq(pageable)))
				.thenReturn(new PageImpl<>(List.of(p1), pageable, 1));
		when(produtoImagemRepository.findFirstByProdutoIdAndPrincipalTrue(anyLong()))
				.thenReturn(Optional.empty());

		Page<ProdutoResponse> a = produtoService.listar(null, pageable);
		Page<ProdutoResponse> b = produtoService.listar("   ", pageable);

		assertThat(a.getTotalElements()).isEqualTo(1);
		assertThat(b.getTotalElements()).isEqualTo(1);

		verify(produtoRepository, times(2)).findAll(pageable);
		verify(produtoRepository, never()).findByNomeContainingIgnoreCase(anyString(), any(Pageable.class));
	}

	// ---------------- listarPorId ----------------

	@Test
	void listarPorId_quandoExiste_deveRetornarResponse_comPrincipalUrlQuandoHouverImagem() {
		var categoria = categoria(10L, "Calças", true);
		var p = produto(7L, "P007", "Calça X", categoria, Set.of(cor(1L, "Azul")), true);

		// imagem principal
		var img = produtoImagem(99L, 7L, true);

		when(produtoRepository.findById(7L)).thenReturn(Optional.of(p));
		when(produtoImagemRepository.findFirstByProdutoIdAndPrincipalTrue(7L)).thenReturn(Optional.of(img));

		ProdutoResponse resp = produtoService.listarPorId(7L);

		assertThat(resp.id()).isEqualTo(7L);
		assertThat(resp.imagemPrincipalUrl()).isEqualTo("/produtos/7/imagens/99");
		assertThat(resp.categoria()).isInstanceOf(CategoriaResponse.class);
		assertThat(resp.cores()).isNotEmpty();
	}

	@Test
	void listarPorId_quandoNaoExiste_deveLancarEntityNotFoundException() {
		when(produtoRepository.findById(123L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> produtoService.listarPorId(123L))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessageContaining("Produto não encontrado");
	}

	// ---------------- salvar ----------------

	@Test
	void salvar_quandoCodigoJaExiste_deveLancarConflictException_eNaoChamarRabbit() {
		var req = new ProdutoRequest(
				"COD-001",
				"Camiseta Dry Fit",
				"Vestuário",
				Set.of(1L, 2L, 3L),
				"M, G, GG",
				new BigDecimal("79.90"),
				true,
				"Camiseta leve e confortável."
		);

		when(produtoRepository.findByCodigoIgnoreCase("COD-001"))
				.thenReturn(Optional.of(produto(1L, "COD-001", "Já existe", categoria(1L, "X", true), Set.of(), true)));

		assertThatThrownBy(() -> produtoService.salvar(req))
				.isInstanceOf(ConflictException.class)
				.hasMessageContaining("Código de produto existente");

		verify(produtoRepository, never()).save(any());
		verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), Optional.ofNullable(any()));
	}

	@Test
	void salvar_quandoCategoriaNaoExiste_deveLancarNotFoundException() {
		var req = new ProdutoRequest(
				"COD-001",
				"Camiseta Dry Fit",
				"Inexistente",
				Set.of(1L, 2L, 3L),
				"M, G, GG",
				new BigDecimal("79.90"),
				true,
				"Camiseta leve e confortável."
		);

		when(produtoRepository.findByCodigoIgnoreCase("COD-001")).thenReturn(Optional.empty());
		when(categoriaRepository.findByNomeIgnoreCase("Inexistente")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> produtoService.salvar(req))
				.isInstanceOf(NotFoundException.class)
				.hasMessageContaining("Categoria não encontrada");

		verify(produtoRepository, never()).save(any());
		verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), Optional.ofNullable(any()));
	}

	@Test
	void salvar_quandoAlgumaCorNaoExiste_deveLancarEntityNotFoundException() {
		var req = new ProdutoRequest(
				"COD-001",
				"Camiseta Dry Fit",
				"Camisas",
				Set.of(1L),
				"M, G, GG",
				new BigDecimal("79.90"),
				true,
				"Camiseta leve e confortável."
		);

		when(produtoRepository.findByCodigoIgnoreCase("COD-001")).thenReturn(Optional.empty());
		when(categoriaRepository.findByNomeIgnoreCase("Camisas")).thenReturn(Optional.of(categoria(10L, "Camisas", true)));
		when(corRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> produtoService.salvar(req))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessageContaining("Cor não encontrada");

		verify(produtoRepository, never()).save(any());
		verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
	}

	@Test
	void salvar_fluxoFeliz_deveSalvarProduto_ePublicarEventoNoRabbit() {

		var req = new ProdutoRequest(
				"COD-001",
				"Camiseta Dry Fit",
				"Camisas",
				Set.of(1L, 2L),
				"M, G, GG",
				new BigDecimal("79.90"),
				true,
				"Camiseta leve e confortável."
		);

		var categoria = categoria(10L, "Camisas", true);
		var azul = cor(1L, "Azul");
		var vermelho = cor(2L, "Vermelho");

		when(produtoRepository.findByCodigoIgnoreCase("COD-001")).thenReturn(Optional.empty());
		when(categoriaRepository.findByNomeIgnoreCase("Camisas")).thenReturn(Optional.of(categoria));
		when(corRepository.findById(1L)).thenReturn(Optional.of(azul));
		when(corRepository.findById(2L)).thenReturn(Optional.of(vermelho));

		when(produtoRepository.save(any(Produto.class))).thenAnswer(inv -> {
			Produto p = inv.getArgument(0);
			p.setId(99L);
			return p;
		});

		when(produtoImagemRepository.findFirstByProdutoIdAndPrincipalTrue(99L))
				.thenReturn(Optional.empty());

		ProdutoResponse resp = produtoService.salvar(req);

		// valida que salvou o produto coerente
		verify(produtoRepository).save(produtoCaptor.capture());
		Produto salvo = produtoCaptor.getValue();
		assertThat(salvo.getCodigo()).isEqualTo("COD-001");
		assertThat(salvo.getNome()).isEqualTo("Camiseta Dry Fit");
		assertThat(salvo.getCategoria().getId()).isEqualTo(10L);
		assertThat(salvo.getCores()).hasSize(2);

		// valida rabbit
		verify(rabbitTemplate).convertAndSend(
				eq(RabbitMQConfig.PRODUTO_EXCHANGE),
				eq(RabbitMQConfig.PRODUTO_CRIADO_ROUTING_KEY),
				eventoCaptor.capture()
		);

		assertThat(eventoCaptor.getValue()).isInstanceOf(ProdutoCriadoEvent.class);
		ProdutoCriadoEvent evento = (ProdutoCriadoEvent) eventoCaptor.getValue();
		assertThat(evento.produtoId()).isEqualTo(99L);
		assertThat(evento.codigo()).isEqualTo("COD-001");
		assertThat(evento.nome()).isEqualTo("Camiseta Dry Fit");
		assertThat(evento.ativo()).isEqualTo(req.ativo());

		assertThat(resp.id()).isEqualTo(99L);
	}

	// ---------------- atualizar ----------------

	@Test
	void atualizar_quandoProdutoNaoExiste_deveLancarEntityNotFoundException() {
		when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> produtoService.atualizar(1L,
				produtoRequest("C1", "N", "Cat", Set.of(1L))))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessageContaining("Produto não encontrado");
	}

	@Test
	void atualizar_quandoCodigoMudaEJaExiste_deveLancarIllegalArgumentException_eNaoSalvar() {
		var existente = produto(1L, "OLD", "Produto", categoria(10L, "Camisas", true), Set.of(cor(1L, "Azul")), true);

		when(produtoRepository.findById(1L)).thenReturn(Optional.of(existente));
		when(produtoRepository.findByCodigoIgnoreCase("NEW"))
				.thenReturn(Optional.of(produto(2L, "NEW", "Outro", categoria(10L, "Camisas", true), Set.of(), true)));

		assertThatThrownBy(() -> produtoService.atualizar(1L, produtoRequest("NEW", "Produto", "Camisas", Set.of(1L))))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Código já existe");

		verify(produtoRepository, never()).save(any());
	}

	@Test
	void atualizar_fluxoFeliz_deveAtualizarCampos_eSalvar() {
		var catAntiga = categoria(10L, "Camisas", true);
		var existente = produto(1L, "COD-001", "Produto Antigo", catAntiga, Set.of(cor(1L, "Azul")), true);

		var catNova = categoria(20L, "Calças", true);
		var corNova = cor(2L, "Vermelho");

		var req = new ProdutoRequest(
				"COD-001",
				"Produto Novo",
				"Calças",
				Set.of(2L),
				"M, G, GG",
				new BigDecimal("79.90"),
				true,
				"Camiseta leve e confortável."
		);

		when(produtoRepository.findById(1L)).thenReturn(Optional.of(existente));
		// codigo não muda => não consulta findByCodigoIgnoreCase
		when(categoriaRepository.findByNomeIgnoreCase("Calças")).thenReturn(Optional.of(catNova));
		when(corRepository.findById(2L)).thenReturn(Optional.of(corNova));
		when(produtoRepository.save(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));
		when(produtoImagemRepository.findFirstByProdutoIdAndPrincipalTrue(1L)).thenReturn(Optional.empty());

		ProdutoResponse resp = produtoService.atualizar(1L, req);

		verify(produtoRepository).save(produtoCaptor.capture());
		Produto salvo = produtoCaptor.getValue();

		assertThat(salvo.getCodigo()).isEqualTo("COD-001");
		assertThat(salvo.getNome()).isEqualTo("Produto Novo");
		assertThat(salvo.getCategoria().getId()).isEqualTo(20L);
		assertThat(salvo.getCores()).extracting("id").containsExactly(2L);

		assertThat(resp.nome()).isEqualTo("Produto Novo");
		assertThat(resp.categoria().id()).isEqualTo(20L);
		verify(produtoRepository, never()).findByCodigoIgnoreCase(anyString());
	}

	// ---------------- desativar ----------------

	@Test
	void desativar_quandoExiste_deveSetarAtivoFalse_eSalvar() {
		var existente = produto(1L, "COD1", "Produto", categoria(10L, "Camisas", true), Set.of(cor(1L, "Azul")), true);

		when(produtoRepository.findById(1L)).thenReturn(Optional.of(existente));
		when(produtoRepository.save(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));
		when(produtoImagemRepository.findFirstByProdutoIdAndPrincipalTrue(1L)).thenReturn(Optional.empty());

		ProdutoResponse resp = produtoService.desativar(1L);

		verify(produtoRepository).save(produtoCaptor.capture());
		assertThat(produtoCaptor.getValue().getAtivo()).isFalse();
		assertThat(resp.ativo()).isFalse();
	}

	@Test
	void desativar_quandoNaoExiste_deveLancarEntityNotFoundException() {
		when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> produtoService.desativar(1L))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessageContaining("Produto não encontrado");
	}

	// ---------------- deletar ----------------

	@Test
	void deletar_quandoExiste_deveDeletarPorId() {
		when(produtoRepository.findById(1L)).thenReturn(Optional.of(
				produto(1L, "COD1", "Produto", categoria(10L, "Camisas", true), Set.of(), true)
		));

		produtoService.deletar(1L);

		verify(produtoRepository).deleteById(1L);
	}

	@Test
	void deletar_quandoNaoExiste_deveLancarEntityNotFoundException_eNaoDeletar() {
		when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> produtoService.deletar(1L))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessageContaining("Produto não encontrado");

		verify(produtoRepository, never()).deleteById(anyLong());
	}

	// ---------------- helpers (ajuste conforme suas entidades) ----------------

	private ProdutoRequest produtoRequest(String codigo, String nome, String categoriaNome, Set<Long> corIds) {

		return new ProdutoRequest(
				codigo,
				nome,
				categoriaNome,
				corIds,
				"M",
				new BigDecimal("10.50"),
				true,
				"Descricao"
		);
	}

	private Categoria categoria(Long id, String nome, boolean ativo) {

		return Categoria.builder()
				.id(id)
				.nome(nome)
				.ativo(ativo)
				.criadoEm(OffsetDateTime.now())
				.atualizadoEm(OffsetDateTime.now())
				.build();
	}

	private Cor cor(Long id, String nome) {

		return Cor.builder()
				.id(id)
				.nome(nome)
				.hex("#000000")
				.ativo(true)
				.criadoEm(OffsetDateTime.now())
				.atualizadoEm(OffsetDateTime.now())
				.build();
	}

	private Produto produto(Long id, String codigo, String nome,
							Categoria categoria,
							Set<Cor> cores,
							boolean ativo) {

		return Produto.builder()
				.id(id)
				.codigo(codigo)
				.nome(nome)
				.categoria(categoria)
				.cores(cores)
				.medidas("M")
				.precoUnitario(new BigDecimal("10.50"))
				.ativo(ativo)
				.descricao("Descricao")
				.criadoEm(OffsetDateTime.now())
				.atualizadoEm(OffsetDateTime.now())
				.build();
	}

	private ProdutoImagem produtoImagem(Long id, Long produtoId, boolean principal) {
		Produto produto = produtoRepository.getById(produtoId);

		return ProdutoImagem.builder()
				.id(id)
				.produto(produto)
				.principal(principal)
				.build();
	}
}
