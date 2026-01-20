package com.sistema.proposta_service.api;

import com.sistema.proposta_service.api.dto.ClienteDTO;
import com.sistema.proposta_service.api.dto.ProdutoPropostaDTO;
import com.sistema.proposta_service.api.dto.PropostaRequest;
import com.sistema.proposta_service.api.dto.PropostaResponse;
import com.sistema.proposta_service.aplicacao.PropostaService;
import com.sistema.proposta_service.config.crypto.CryptoService;
import com.sistema.proposta_service.dominio.Cliente;
import com.sistema.proposta_service.dominio.ProdutoProposta;
import com.sistema.proposta_service.dominio.Proposta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/propostas")
@RequiredArgsConstructor
public class PropostaController {

    private final PropostaService service;
    private final CryptoService crypto;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public ResponseEntity<Page<PropostaResponse>> listar(Pageable pageable) {
        Page<PropostaResponse> page = service.listar(pageable).map(this::toResponse);
        return ResponseEntity.ok(page);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<PropostaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(service.buscarPorId(id)));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<PropostaResponse> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(toResponse(service.buscarPorCodigo(codigo)));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<PropostaResponse> criar(
            @Valid @RequestBody PropostaRequest request,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorization
    ) {
        Proposta proposta = toEntity(request);
        Proposta salva = service.salvar(proposta, authorization);
        return ResponseEntity.ok(toResponse(salva));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
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
                .dataCriacao(proposta.getDataCriacao())
                .build();
    }

    private Proposta toEntity(PropostaRequest request) {
        Cliente cliente = toClienteEntityFromDtoPlain(request.getCliente());

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

    /**
     * ✅ DTO -> Entidade (entrada em claro)
     * Aqui nós colocamos os valores recebidos nos campos "*Legacy"
     * para o service criptografar e gerar hash antes de persistir.
     */
    private Cliente toClienteEntityFromDtoPlain(ClienteDTO dto) {
        return Cliente.builder()
                .nome(dto.getNome())
                .cpfCnpjLegacy(dto.getCpfCnpj())
                .telefoneLegacy(dto.getTelefone())
                .emailLegacy(dto.getEmail())
                .cepLegacy(dto.getCep())
                .enderecoLegacy(dto.getEndereco())
                .bairroLegacy(dto.getBairro())
                .cidade(dto.getCidade())
                .uf(dto.getUf())
                .referenciaLegacy(dto.getReferencia())
                .complementoLegacy(dto.getComplemento())
                .build();
    }

    /**
     * ✅ Entidade -> DTO (saída descriptografada)
     * Usa enc se existir; senão, fallback para legado (durante migração).
     */
    private ClienteDTO toClienteDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();

        dto.setNome(cliente.getNome());

        // cpf/cnpj
        String cpf = cliente.getCpfCnpjEnc() != null
                ? crypto.decryptOrPassThrough(cliente.getCpfCnpjEnc())
                : cliente.getCpfCnpjLegacy();
        dto.setCpfCnpj(cpf);

        // email
        String email = cliente.getEmailEnc() != null
                ? crypto.decryptOrPassThrough(cliente.getEmailEnc())
                : cliente.getEmailLegacy();
        dto.setEmail(email);

        // telefone
        String tel = cliente.getTelefoneEnc() != null
                ? crypto.decryptOrPassThrough(cliente.getTelefoneEnc())
                : cliente.getTelefoneLegacy();
        dto.setTelefone(tel);

        // demais
        dto.setCep(cliente.getCepEnc() != null ? crypto.decryptOrPassThrough(cliente.getCepEnc()) : cliente.getCepLegacy());
        dto.setEndereco(cliente.getEnderecoEnc() != null ? crypto.decryptOrPassThrough(cliente.getEnderecoEnc()) : cliente.getEnderecoLegacy());
        dto.setBairro(cliente.getBairroEnc() != null ? crypto.decryptOrPassThrough(cliente.getBairroEnc()) : cliente.getBairroLegacy());
        dto.setCidade(cliente.getCidade());
        dto.setUf(cliente.getUf());
        dto.setReferencia(cliente.getReferenciaEnc() != null ? crypto.decryptOrPassThrough(cliente.getReferenciaEnc()) : cliente.getReferenciaLegacy());
        dto.setComplemento(cliente.getComplementoEnc() != null ? crypto.decryptOrPassThrough(cliente.getComplementoEnc()) : cliente.getComplementoLegacy());

        return dto;
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
