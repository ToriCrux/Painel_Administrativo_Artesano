package com.sistema.admin.proposta.aplicacao;

import com.sistema.admin.proposta.dominio.Cliente;
import com.sistema.admin.proposta.dominio.Proposta;
import com.sistema.admin.proposta.infra.ClienteRepository;
import com.sistema.admin.proposta.infra.PropostaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PropostaService {

    private final PropostaRepository repository;
    private final ClienteRepository clienteRepository;

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
    
    @Transactional
    public Proposta salvar(Proposta proposta) {
        Cliente cliente = proposta.getCliente();

        // Verifica se o cliente já existe
        Cliente clienteExistente = clienteRepository.findByCpfCnpj(cliente.getCpfCnpj())
                .orElseGet(() -> {
                    // Se não existir, salva o novo cliente
                    return clienteRepository.save(cliente);
                });

        // Define o cliente (novo ou existente)
        proposta.setCliente(clienteExistente);

        // Recalcula total e salva a proposta
        proposta.recalcularTotal();
        return repository.save(proposta);
    }

    public void deletar(Long id) {
        Proposta proposta = buscarPorId(id);
        repository.delete(proposta);
    }

    public Page<Proposta> buscarPorVendedor(String nomeVendedor, Pageable pageable) {
        return repository.findByNomeVendedorContainingIgnoreCase(nomeVendedor, pageable);
    }
}
