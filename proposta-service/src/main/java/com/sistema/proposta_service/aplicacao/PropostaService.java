package com.sistema.proposta_service.aplicacao;

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

    /**
     * Salva proposta e publica evento (envio ao Rabbit será AFTER_COMMIT no listener),
     * carregando o Authorization do usuário que criou a proposta.
     */
    @Transactional
    public Proposta salvar(Proposta proposta, String authorization) {
        if (proposta == null || proposta.getCliente() == null) {
            throw new IllegalArgumentException("Cliente é obrigatório para criar a proposta (ao menos CPF/CNPJ, nome e email).");
        }

        Cliente recebido = proposta.getCliente();
        String cpfCnpj = recebido.getCpfCnpj();

        if (cpfCnpj == null || cpfCnpj.isBlank()) {
            throw new IllegalArgumentException("CPF/CNPJ é obrigatório para criar a proposta.");
        }
        if (recebido.getNome() == null || recebido.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório.");
        }
        if (recebido.getEmail() == null || recebido.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email do cliente é obrigatório.");
        }

        // 1) Upsert do cliente por CPF/CNPJ
        Cliente clienteGerenciado = clienteRepository.findByCpfCnpj(cpfCnpj)
                .map(existente -> {
                    existente.aplicarAtualizacoesSeVieram(recebido);
                    return existente;
                })
                .orElseGet(() -> Cliente.novo(recebido));

        clienteGerenciado = clienteRepository.save(clienteGerenciado);

        // 2) Amarra a proposta ao cliente gerenciado
        proposta.setCliente(clienteGerenciado);

        // 3) Garante proposta <-> produtos + subtotal/total
        proposta.amarrarProdutos();

        // 4) Salva (cascade salva os produtos)
        Proposta salva = repository.save(proposta);

        // 5) Dispara evento (o envio pro Rabbit acontecerá AFTER_COMMIT via listener)
        eventPublisher.publishEvent(new PropostaCriadaDomainEvent(salva.getId(), authorization));

        return salva;
    }

    /**
     * Mantém compatibilidade com chamadas internas (sem Authorization).
     * Se você não tiver nenhum uso interno, pode remover.
     */
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
