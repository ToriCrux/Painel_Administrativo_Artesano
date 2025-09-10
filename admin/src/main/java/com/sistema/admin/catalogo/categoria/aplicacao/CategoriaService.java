package com.sistema.admin.catalogo.categoria.aplicacao;


import com.sistema.admin.catalogo.categoria.dominio.Categoria;
import com.sistema.admin.catalogo.categoria.infra.CategoriaRepository;
import com.sistema.admin.catalogo.categoria.api.dto.CategoriaRequest;
import com.sistema.admin.catalogo.categoria.api.dto.CategoriaResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository repository;

    public Page<CategoriaResponse> listar(String nome, Pageable pageable) {
        Page<Categoria> page = (nome != null && !nome.isBlank())
                ? repository.findByNomeContainingIgnoreCase(nome, pageable)
                : repository.findAll(pageable);

        return page.map(this::toResponse);
    }

    public CategoriaResponse salvar(CategoriaRequest dto) {
        repository.findByNomeIgnoreCase(dto.nome())
                .ifPresent(c -> { throw new IllegalArgumentException("Categoria já existe"); });

        Categoria categoria = new Categoria();
        categoria.setNome(dto.nome());
        categoria.setAtivo(dto.ativo());

        Categoria saved = repository.save(categoria);
        return toResponse(saved);
    }

    public CategoriaResponse atualizar(Long id, CategoriaRequest dto) {
        Categoria existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));

        if (!existente.getNome().equalsIgnoreCase(dto.nome())) {
            repository.findByNomeIgnoreCase(dto.nome())
                    .ifPresent(c -> { throw new IllegalArgumentException("Categoria já existe"); });
        }

        existente.setNome(dto.nome());
        existente.setAtivo(dto.ativo());

        Categoria updated = repository.save(existente);
        return toResponse(updated);
    }

    public void deletar(Long id) {
        Categoria categoria = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));
        categoria.setAtivo(false); // soft delete
        repository.save(categoria);
    }

    private CategoriaResponse toResponse(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getAtivo(),
                categoria.getCriadoEm(),
                categoria.getAtualizadoEm()
        );
    }
}
