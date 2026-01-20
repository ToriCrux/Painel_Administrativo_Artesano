package com.sistema.proposta_service.aplicacao;

import com.sistema.proposta_service.config.crypto.CryptoService;
import com.sistema.proposta_service.dominio.Cliente;
import com.sistema.proposta_service.dominio.Proposta;
import com.sistema.proposta_service.infra.ClienteRepository;
import com.sistema.proposta_service.infra.PropostaRepository;
import com.sistema.proposta_service.infra.mensageria.evento.PropostaCriadaDomainEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PropostaService {

    private final PropostaRepository repository;
    private final ClienteRepository clienteRepository;
    private final ApplicationEventPublisher eventPublisher;

    private final CryptoService crypto;

    @Transactional
    public Proposta salvar(Proposta proposta, String authorization) {
        if (proposta == null || proposta.getCliente() == null) {
            throw new IllegalArgumentException("Cliente é obrigatório para criar a proposta (ao menos CPF/CNPJ, nome e email).");
        }

        // ----------------------------
        // 1) validações (em claro vindo do DTO)
        // ----------------------------
        Cliente recebidoPlain = proposta.getCliente();

        String cpfCnpjPlain = recebidoPlain.getCpfCnpjLegacy(); // ⚠ vem do controller (ver abaixo)
        String nomePlain = recebidoPlain.getNome();
        String emailPlain = recebidoPlain.getEmailLegacy();      // ⚠ vem do controller (ver abaixo)

        if (cpfCnpjPlain == null || cpfCnpjPlain.isBlank()) {
            throw new IllegalArgumentException("CPF/CNPJ é obrigatório para criar a proposta.");
        }
        if (nomePlain == null || nomePlain.isBlank()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório.");
        }
        if (emailPlain == null || emailPlain.isBlank()) {
            throw new IllegalArgumentException("Email do cliente é obrigatório.");
        }

        // ----------------------------
        // 2) calcula hash para upsert
        // ----------------------------
        String cpfNorm = crypto.normalize(cpfCnpjPlain);
        String cpfHash = crypto.hmacSha256Hex(cpfNorm);

        // ----------------------------
        // 3) monta "cliente criptografado" (sem depender do JPA converter)
        // ----------------------------
        Cliente recebidoCripto = Cliente.builder()
                .nome(nomePlain)

                .cpfCnpjEnc(crypto.encrypt(cpfCnpjPlain))
                .cpfCnpjHash(cpfHash)

                .telefoneEnc(crypto.encrypt(recebidoPlain.getTelefoneLegacy()))
                .telefoneHash(crypto.hmacSha256Hex(crypto.normalize(recebidoPlain.getTelefoneLegacy())))

                .emailEnc(crypto.encrypt(emailPlain))
                .emailHash(crypto.hmacSha256Hex(crypto.normalize(emailPlain)))

                .cepEnc(crypto.encrypt(recebidoPlain.getCepLegacy()))
                .enderecoEnc(crypto.encrypt(recebidoPlain.getEnderecoLegacy()))
                .numeroEnc(crypto.encrypt(recebidoPlain.getNumeroLegacy()))
                .complementoEnc(crypto.encrypt(recebidoPlain.getComplementoLegacy()))
                .bairroEnc(crypto.encrypt(recebidoPlain.getBairroLegacy()))
                .cidade(recebidoPlain.getCidade())
                .uf(recebidoPlain.getUf())
                .referenciaEnc(crypto.encrypt(recebidoPlain.getReferenciaLegacy()))
                .build();

        // ----------------------------
        // 4) upsert: tenta por hash; se ainda não tiver hash legado, tenta por cpf legado (fase migração)
        // ----------------------------
        Cliente clienteGerenciado = clienteRepository.findByCpfCnpjHash(cpfHash)
                .or(() -> clienteRepository.findByCpfCnpjLegacy(cpfCnpjPlain))
                .map(existente -> {
                    existente.aplicarAtualizacoesSeVieramCriptografadas(recebidoCripto);
                    return existente;
                })
                .orElseGet(() -> Cliente.novoCriptografado(recebidoCripto));

        // ----------------------------
        // 5) salva cliente
        // ----------------------------
        clienteGerenciado = clienteRepository.save(clienteGerenciado);

        // ----------------------------
        // 6) amarra proposta e produtos
        // ----------------------------
        proposta.setCliente(clienteGerenciado);
        proposta.amarrarProdutos();

        // ----------------------------
        // 7) salva proposta
        // ----------------------------
        Proposta salva = repository.save(proposta);

        // ----------------------------
        // 8) evento AFTER_COMMIT (como você já fez)
        // ----------------------------
        eventPublisher.publishEvent(new PropostaCriadaDomainEvent(salva.getId(), authorization));

        return salva;
    }

    @Transactional
    public Proposta salvar(Proposta proposta) {
        return salvar(proposta, null);
    }

    public Page<Proposta> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Proposta buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proposta não encontrada com id: " + id));
    }

    public Proposta buscarPorCodigo(String codigo) {
        return repository.findByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundException("Proposta não encontrada com código: " + codigo));
    }

    public void deletar(Long id) {
        Proposta proposta = buscarPorId(id);
        repository.delete(proposta);
    }

    public Page<Proposta> buscarPorVendedor(String nomeVendedor, Pageable pageable) {
        return repository.findByNomeVendedorContainingIgnoreCase(nomeVendedor, pageable);
    }
}
