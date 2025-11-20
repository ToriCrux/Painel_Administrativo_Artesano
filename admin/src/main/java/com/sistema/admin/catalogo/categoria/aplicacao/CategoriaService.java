package com.sistema.admin.catalogo.categoria.aplicacao;


import com.sistema.admin.catalogo.categoria.dominio.Categoria;
import com.sistema.admin.catalogo.categoria.infra.CategoriaRepository;
import com.sistema.admin.catalogo.categoria.api.dto.CategoriaRequest;
import com.sistema.admin.catalogo.categoria.api.dto.CategoriaResponse;
import com.sistema.admin.config.exception.ConflictException;
import com.sistema.admin.config.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public Page<CategoriaResponse> listar(String nome, Pageable pageable) {
        Page<Categoria> page = (nome != null && !nome.isBlank())
                ? categoriaRepository.findByNomeContainingIgnoreCase(nome, pageable)
                : categoriaRepository.findAll(pageable);

        return page.map(this::toResponse);
    }

    public CategoriaResponse listarPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada para este id"));
        return toResponse(categoria);
    }

    public CategoriaResponse salvar(CategoriaRequest categoriaRequest) {
        categoriaRepository.findByNomeIgnoreCase(categoriaRequest.nome())
                .ifPresent(c -> { throw new ConflictException("Categoria já existe"); });

        Categoria categoria = new Categoria();
        categoria.setNome(categoriaRequest.nome());
        categoria.setAtivo(categoriaRequest.ativo());

        return toResponse(categoriaRepository.save(categoria));
    }

    public CategoriaResponse atualizar(Long id, CategoriaRequest categoriaRequest) {
        Categoria existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));

        if (!existente.getNome().equalsIgnoreCase(categoriaRequest.nome())) {
            categoriaRepository.findByNomeIgnoreCase(categoriaRequest.nome())
                    .ifPresent(c -> { throw new ConflictException("Categoria já existe"); });
        }

        existente.setNome(categoriaRequest.nome());
        existente.setAtivo(categoriaRequest.ativo());

        return toResponse(categoriaRepository.save(existente));
    }

    public CategoriaResponse desativar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));
        categoria.setAtivo(false); // soft delete
        return toResponse(categoriaRepository.save(categoria));
    }

    public void deletar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));
        categoriaRepository.delete(categoria);
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
