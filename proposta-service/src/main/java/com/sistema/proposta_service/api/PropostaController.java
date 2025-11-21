package com.sistema.proposta_service.api;

import com.sistema.proposta_service.api.dto.ClienteDTO;
import com.sistema.proposta_service.api.dto.ProdutoPropostaDTO;
import com.sistema.proposta_service.api.dto.PropostaRequest;
import com.sistema.proposta_service.api.dto.PropostaResponse;
import com.sistema.proposta_service.aplicacao.PropostaService;
import com.sistema.proposta_service.dominio.Cliente;
import com.sistema.proposta_service.dominio.ProdutoProposta;
import com.sistema.proposta_service.dominio.Proposta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/propostas")
@RequiredArgsConstructor
public class PropostaController {

    private final PropostaService service;

    //@PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public ResponseEntity<Page<PropostaResponse>> listar(Pageable pageable) {
        Page<PropostaResponse> page = service.listar(pageable)
                .map(this::toResponse);
        return ResponseEntity.ok(page);
    }

    //@PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<PropostaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(service.buscarPorId(id)));
    }

    //@PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<PropostaResponse> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(toResponse(service.buscarPorCodigo(codigo)));
    }

    //@PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<PropostaResponse> criar(@Valid @RequestBody PropostaRequest request) {
        Proposta proposta = toEntity(request);
        return ResponseEntity.ok(toResponse(service.salvar(proposta)));
    }

    //@PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------
    // Conversões DTO ↔ Entidade
    // -------------------------------

    private PropostaResponse toResponse(Proposta proposta) {
        return PropostaResponse.builder()
                .id(proposta.getId())
                .codigo(proposta.getCodigo())
                .nomeVendedor(proposta.getNomeVendedor())
                .dataProposta(proposta.getDataProposta())
                .dataValidade(proposta.getDataValidade())
                .total(proposta.getTotal())
                .cliente(toClienteDTO(proposta.getCliente()))
                .produtos(proposta.getProdutos().stream()
                        .map(this::toProdutoDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    private Proposta toEntity(PropostaRequest request) {
        Cliente cliente = toClienteEntity(request.getCliente());

        Proposta proposta = Proposta.builder()
                .codigo(request.getCodigo())
                .nomeVendedor(request.getNomeVendedor())
                .dataProposta(request.getDataProposta())
                .dataValidade(request.getDataValidade())
                .cliente(cliente)
                .build();

        request.getProdutos().forEach(prodDto -> {
            ProdutoProposta produto = toProdutoEntity(prodDto);
            proposta.adicionarProduto(produto);
        });

        return proposta;
    }

    private ClienteDTO toClienteDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setNome(cliente.getNome());
        dto.setCpfCnpj(cliente.getCpfCnpj());
        dto.setTelefone(cliente.getTelefone());
        dto.setEmail(cliente.getEmail());
        dto.setCep(cliente.getCep());
        dto.setEndereco(cliente.getEndereco());
        dto.setBairro(cliente.getBairro());
        dto.setCidade(cliente.getCidade());
        dto.setUf(cliente.getUf());
        dto.setReferencia(cliente.getReferencia());
        dto.setComplemento(cliente.getComplemento());
        return dto;
    }

    private Cliente toClienteEntity(ClienteDTO dto) {
        return Cliente.builder()
                .nome(dto.getNome())
                .cpfCnpj(dto.getCpfCnpj())
                .telefone(dto.getTelefone())
                .email(dto.getEmail())
                .cep(dto.getCep())
                .endereco(dto.getEndereco())
                .bairro(dto.getBairro())
                .cidade(dto.getCidade())
                .uf(dto.getUf())
                .referencia(dto.getReferencia())
                .complemento(dto.getComplemento())
                .build();
    }

    private ProdutoPropostaDTO toProdutoDTO(ProdutoProposta produto) {
        ProdutoPropostaDTO dto = new ProdutoPropostaDTO();
        dto.setCodigoProduto(produto.getCodigoProduto());
        dto.setNomeProduto(produto.getNomeProduto());
        dto.setQuantidade(produto.getQuantidade());
        dto.setPrecoUnitario(produto.getPrecoUnitario());
        return dto;
    }

    private ProdutoProposta toProdutoEntity(ProdutoPropostaDTO dto) {
        return ProdutoProposta.builder()
                .codigoProduto(dto.getCodigoProduto())
                .nomeProduto(dto.getNomeProduto())
                .quantidade(dto.getQuantidade())
                .precoUnitario(dto.getPrecoUnitario())
                .build();
    }
}
