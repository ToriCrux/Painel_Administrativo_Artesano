package com.sistema.admin.catalogo.aplicacao;

import com.sistema.admin.catalogo.dominio.Cor;
import com.sistema.admin.catalogo.infra.CorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CorService {

    private final CorRepository repository;

    public Page<Cor> listar(String nome, Pageable pageable) {
        if (nome != null && !nome.isBlank()) {
            return repository.findByNomeContainingIgnoreCase(nome, pageable);
        }
        return repository.findAll(pageable);
    }

    public Cor salvar(Cor cor) {
        repository.findByNomeIgnoreCase(cor.getNome())
                .ifPresent(c -> { throw new IllegalArgumentException("Cor já existe"); });
        return repository.save(cor);
    }

    public Cor atualizar(Long id, Cor dados) {
        Cor existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cor não encontrada"));

        if (!existente.getNome().equalsIgnoreCase(dados.getNome())) {
            repository.findByNomeIgnoreCase(dados.getNome())
                    .ifPresent(c -> { throw new IllegalArgumentException("Cor já existe"); });
        }

        existente.setNome(dados.getNome());
        existente.setHex(dados.getHex());
        existente.setAtivo(dados.getAtivo());
        return repository.save(existente);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
