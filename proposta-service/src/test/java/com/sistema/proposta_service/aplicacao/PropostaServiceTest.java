
package com.sistema.proposta_service.aplicacao;

import com.sistema.proposta_service.dominio.Cliente;
import com.sistema.proposta_service.dominio.ProdutoProposta;
import com.sistema.proposta_service.dominio.Proposta;
import com.sistema.proposta_service.infra.ClienteRepository;
import com.sistema.proposta_service.infra.PropostaRepository;
import jakarta.persistence.EntityNotFoundException;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes simples de unidade para o {@link PropostaService}.
 *
 * A ideia aqui é só garantir que o service:
 *  - delega corretamente para o repositório
 *  - lança exception quando não encontra
 *  - recalcula o total e associa o cliente antes de salvar
 *
 * Não é para ser um teste super elaborado, é só para cobrir os fluxos principais.
 */
@ExtendWith(MockitoExtension.class)
class PropostaServiceTest {

    @Mock
    PropostaRepository propostaRepository;

    @Mock
    ClienteRepository clienteRepository;

    @InjectMocks
    PropostaService service;

    private Proposta novaPropostaComProdutos() {
        Cliente cliente = Cliente.builder()
                .nome("Cliente Teste")
                .cpfCnpj("12345678900")
                .telefone("61999990000")
                .email("cliente@teste.com")
                .build();

        ProdutoProposta prod1 = ProdutoProposta.builder()
                .codigoProduto("P1")
                .nomeProduto("Produto 1")
                .quantidade(2)
                .precoUnitario(new BigDecimal("10.00"))
                .build();
        prod1.calcularSubtotal();

        ProdutoProposta prod2 = ProdutoProposta.builder()
                .codigoProduto("P2")
                .nomeProduto("Produto 2")
                .quantidade(1)
                .precoUnitario(new BigDecimal("5.00"))
                .build();
        prod2.calcularSubtotal();

        Proposta proposta = Proposta.builder()
                .codigo("PROP-1")
                .nomeVendedor("Vendedor 1")
                .dataProposta(LocalDate.of(2025, 1, 10))
                .dataValidade(LocalDate.of(2025, 1, 20))
                .cliente(cliente)
                .build();

        // garante que a lista de produtos está populada e o total será > 0
        proposta.adicionarProduto(prod1);
        proposta.adicionarProduto(prod2);
        return proposta;
    }

    @Test
    @DisplayName("listar deve delegar para o repositório")
    void listar_deveDelegarParaRepositorio() {
        Pageable pageable = PageRequest.of(0, 10);
        Proposta proposta = novaPropostaComProdutos();
        Page<Proposta> page = new PageImpl<>(List.of(proposta), pageable, 1);

        when(propostaRepository.findAll(pageable)).thenReturn(page);

        Page<Proposta> result = service.listar(pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).containsExactly(proposta);
        verify(propostaRepository).findAll(pageable);
    }

    @Test
    @DisplayName("buscarPorId deve retornar proposta quando existir")
    void buscarPorId_quandoExiste() {
        Proposta proposta = novaPropostaComProdutos();
        when(propostaRepository.findById(1L)).thenReturn(Optional.of(proposta));

        Proposta result = service.buscarPorId(1L);

        assertThat(result).isSameAs(proposta);
        verify(propostaRepository).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId deve lançar EntityNotFoundException quando não existir")
    void buscarPorId_quandoNaoExiste_deveLancarException() {
        when(propostaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(EntityNotFoundException.class);

        verify(propostaRepository).findById(99L);
    }

    @Test
    @DisplayName("buscarPorCodigo deve delegar para o repositório")
    void buscarPorCodigo_deveDelegarParaRepositorio() {
        Proposta proposta = novaPropostaComProdutos();
        when(propostaRepository.findByCodigo("COD-123")).thenReturn(Optional.of(proposta));

        Proposta result = service.buscarPorCodigo("COD-123");

        assertThat(result).isSameAs(proposta);
        verify(propostaRepository).findByCodigo("COD-123");
    }

    @Test
    @DisplayName("deletar deve buscar a proposta e apagar do repositório")
    void deletar_deveBuscarEDeletar() {
        Proposta proposta = novaPropostaComProdutos();
        when(propostaRepository.findById(10L)).thenReturn(Optional.of(proposta));

        service.deletar(10L);

        verify(propostaRepository).findById(10L);
        verify(propostaRepository).delete(proposta);
    }

    @Test
    @DisplayName("buscarPorVendedor deve delegar para findByNomeVendedorContainingIgnoreCase")
    void buscarPorVendedor_deveDelegarParaRepositorio() {
        Pageable pageable = PageRequest.of(0, 5);
        Proposta proposta = novaPropostaComProdutos();
        Page<Proposta> page = new PageImpl<>(List.of(proposta), pageable, 1);

        when(propostaRepository.findByNomeVendedorContainingIgnoreCase("vend", pageable))
                .thenReturn(page);

        Page<Proposta> result = service.buscarPorVendedor("vend", pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(propostaRepository)
                .findByNomeVendedorContainingIgnoreCase("vend", pageable);
    }

    @Test
    @DisplayName("salvar deve reaproveitar cliente existente, recalcular total e salvar")
    void salvar_deveReaproveitarClienteERecalcularTotal() {
        Proposta proposta = novaPropostaComProdutos();

        Cliente clienteExistente = Cliente.builder()
                .id(100L)
                .nome("Cliente Banco")
                .cpfCnpj(proposta.getCliente().getCpfCnpj())
                .telefone(proposta.getCliente().getTelefone())
                .email(proposta.getCliente().getEmail())
                .build();

        when(clienteRepository.findByCpfCnpj(proposta.getCliente().getCpfCnpj()))
                .thenReturn(Optional.of(clienteExistente));
        when(propostaRepository.save(any(Proposta.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Proposta salvo = service.salvar(proposta);

        ArgumentCaptor<Proposta> captor = ArgumentCaptor.forClass(Proposta.class);
        verify(propostaRepository).save(captor.capture());
        Proposta enviadoParaSalvar = captor.getValue();

        // cliente da proposta deve ser o existente do banco
        assertThat(enviadoParaSalvar.getCliente()).isEqualTo(clienteExistente);
        // total deve ser a soma dos subtotais (2x10 + 1x5 = 25)
        assertThat(enviadoParaSalvar.getTotal()).isEqualTo(new BigDecimal("25.00"));
        // e o objeto retornado deve ser o mesmo que foi salvo
        assertThat(salvo).isSameAs(enviadoParaSalvar);
    }
}
