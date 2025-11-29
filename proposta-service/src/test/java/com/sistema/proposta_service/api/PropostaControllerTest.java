
package com.sistema.proposta_service.api;

import com.sistema.proposta_service.api.dto.ClienteDTO;
import com.sistema.proposta_service.api.dto.ProdutoPropostaDTO;
import com.sistema.proposta_service.api.dto.PropostaRequest;
import com.sistema.proposta_service.api.dto.PropostaResponse;
import com.sistema.proposta_service.aplicacao.PropostaService;
import com.sistema.proposta_service.dominio.Cliente;
import com.sistema.proposta_service.dominio.ProdutoProposta;
import com.sistema.proposta_service.dominio.Proposta;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes de unidade para o {@link PropostaController}.
 *
 * Aqui a ideia é somente validar o mapeamento entre:
 *  - request JSON -> entidades de domínio
 *  - entidades de domínio -> response JSON
 * e os status HTTP retornados.
 */
@ExtendWith(MockitoExtension.class)
class PropostaControllerTest {

    @Mock
    PropostaService service;

    @InjectMocks
    PropostaController controller;

    private ClienteDTO novoClienteDTO() {
        ClienteDTO dto = new ClienteDTO();
        dto.setNome("Cliente DTO");
        dto.setCpfCnpj("12345678900");
        dto.setTelefone("61999990000");
        dto.setEmail("cliente@teste.com");
        dto.setCep("70000000");
        dto.setEndereco("Rua Teste");
        dto.setBairro("Bairro");
        dto.setCidade("Brasília");
        dto.setUf("DF");
        dto.setReferencia("Perto de algum lugar");
        dto.setComplemento("Casa");
        return dto;
    }

    private ProdutoPropostaDTO novoProdutoDTO(String codigo, int quantidade, String preco) {
        ProdutoPropostaDTO dto = new ProdutoPropostaDTO();
        dto.setCodigoProduto(codigo);
        dto.setNomeProduto("Produto " + codigo);
        dto.setQuantidade(quantidade);
        dto.setPrecoUnitario(new BigDecimal(preco));
        return dto;
    }

    private Proposta novaPropostaDomain() {
        Cliente cliente = Cliente.builder()
                .nome("Cliente Domain")
                .cpfCnpj("12345678900")
                .telefone("61999990000")
                .email("cliente@teste.com")
                .cep("70000000")
                .endereco("Rua Teste")
                .bairro("Bairro")
                .cidade("Brasília")
                .uf("DF")
                .referencia("Perto de algum lugar")
                .complemento("Casa")
                .build();

        ProdutoProposta p1 = ProdutoProposta.builder()
                .codigoProduto("P1")
                .nomeProduto("Produto 1")
                .quantidade(2)
                .precoUnitario(new BigDecimal("10.00"))
                .build();
        p1.calcularSubtotal();

        ProdutoProposta p2 = ProdutoProposta.builder()
                .codigoProduto("P2")
                .nomeProduto("Produto 2")
                .quantidade(1)
                .precoUnitario(new BigDecimal("5.00"))
                .build();
        p2.calcularSubtotal();

        Proposta proposta = Proposta.builder()
                .codigo("PROP-1")
                .nomeVendedor("Vendedor 1")
                .dataProposta(LocalDate.of(2025, 1, 10))
                .dataValidade(LocalDate.of(2025, 1, 20))
                .cliente(cliente)
                .build();

        proposta.adicionarProduto(p1);
        proposta.adicionarProduto(p2);
        return proposta;
    }

    private PropostaRequest novaPropostaRequest() {
        PropostaRequest req = new PropostaRequest();
        req.setCodigo("PROP-REQ");
        req.setNomeVendedor("Vendedor Request");
        req.setDataProposta(LocalDate.of(2025, 2, 10));
        req.setDataValidade(LocalDate.of(2025, 2, 20));
        req.setCliente(novoClienteDTO());
        req.setProdutos(List.of(
                novoProdutoDTO("P1", 2, "10.00"),
                novoProdutoDTO("P2", 1, "5.00")
        ));
        return req;
    }

    @Test
    @DisplayName("listar deve retornar 200 com página de PropostaResponse")
    void listar_ok() {
        Pageable pageable = PageRequest.of(0, 10);
        Proposta proposta = novaPropostaDomain();
        Page<Proposta> page = new PageImpl<>(List.of(proposta), pageable, 1);

        when(service.listar(pageable)).thenReturn(page);

        ResponseEntity<Page<PropostaResponse>> response = controller.listar(pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Page<PropostaResponse> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getTotalElements()).isEqualTo(1);
        assertThat(body.getContent().get(0).getCodigo()).isEqualTo("PROP-1");
        assertThat(body.getContent().get(0).getCliente().getNome()).isEqualTo("Cliente Domain");
        verify(service).listar(pageable);
    }

    @Test
    @DisplayName("buscarPorId deve retornar 200 com a proposta encontrada")
    void buscarPorId_ok() {
        Proposta proposta = novaPropostaDomain();
        when(service.buscarPorId(10L)).thenReturn(proposta);

        ResponseEntity<PropostaResponse> response = controller.buscarPorId(10L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        PropostaResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getCodigo()).isEqualTo("PROP-1");
        assertThat(body.getCliente().getNome()).isEqualTo("Cliente Domain");
        verify(service).buscarPorId(10L);
    }

    @Test
    @DisplayName("buscarPorCodigo deve retornar 200 com a proposta encontrada")
    void buscarPorCodigo_ok() {
        Proposta proposta = novaPropostaDomain();
        when(service.buscarPorCodigo("CODE-1")).thenReturn(proposta);

        ResponseEntity<PropostaResponse> response = controller.buscarPorCodigo("CODE-1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCodigo()).isEqualTo("PROP-1");
        verify(service).buscarPorCodigo("CODE-1");
    }

    @Test
    @DisplayName("criar deve converter o request em entidade, chamar o service e retornar 200")
    void criar_ok() {
        PropostaRequest req = novaPropostaRequest();
        Proposta propostaSalva = novaPropostaDomain();

        when(service.salvar(any(Proposta.class))).thenReturn(propostaSalva);

        ResponseEntity<PropostaResponse> response = controller.criar(req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        PropostaResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getCodigo()).isEqualTo("PROP-1");
        assertThat(body.getCliente().getNome()).isEqualTo("Cliente Domain");

        ArgumentCaptor<Proposta> captor = ArgumentCaptor.forClass(Proposta.class);
        verify(service).salvar(captor.capture());
        Proposta enviado = captor.getValue();

        assertThat(enviado.getCodigo()).isEqualTo(req.getCodigo());
        assertThat(enviado.getNomeVendedor()).isEqualTo(req.getNomeVendedor());
        assertThat(enviado.getCliente().getNome()).isEqualTo(req.getCliente().getNome());
        assertThat(enviado.getProdutos()).hasSize(2);
    }

    @Test
    @DisplayName("deletar deve chamar o service e retornar 204")
    void deletar_ok() {
        ResponseEntity<Void> response = controller.deletar(99L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(service).deletar(99L);
    }
}
