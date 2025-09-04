package com.sistema.admin.catalogo.aplicacao;

import com.sistema.admin.catalogo.dominio.Categoria;
import com.sistema.admin.catalogo.infra.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository repository;

    public Page<Categoria> listar(String nome, Pageable pageable) {
        if (nome != null && !nome.isBlank()) {
            return repository.findByNomeContainingIgnoreCase(nome, pageable);
        }
        return repository.findAll(pageable);
    }

    public Categoria salvar(Categoria categoria) {
        repository.findByNomeIgnoreCase(categoria.getNome())
                .ifPresent(c -> { throw new IllegalArgumentException("Categoria já existe"); });
        return repository.save(categoria);
    }

    public Categoria atualizar(Long id, Categoria dados) {
        Categoria existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));

        if (!existente.getNome().equalsIgnoreCase(dados.getNome())) {
            repository.findByNomeIgnoreCase(dados.getNome())
                    .ifPresent(c -> { throw new IllegalArgumentException("Categoria já existe"); });
        }

        existente.setNome(dados.getNome());
        existente.setAtivo(dados.getAtivo());
        return repository.save(existente);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
