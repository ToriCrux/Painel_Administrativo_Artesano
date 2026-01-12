package com.sistema.catalogoservice.catalogo.categoria.aplicacao;

import com.sistema.catalogoservice.catalogo.categoria.api.dto.*;
import com.sistema.catalogoservice.catalogo.categoria.dominio.Categoria;
import com.sistema.catalogoservice.catalogo.categoria.infra.CategoriaRepository;
import com.sistema.catalogoservice.config.exception.ConflictException;
import com.sistema.catalogoservice.config.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public Page<CategoriaResponse> listar(String nome, Pageable pageable) {
        Page<Categoria> page;

        if (nome != null && !nome.isBlank()) {
            page = categoriaRepository.findByNomeContainingIgnoreCase(nome, pageable);
        } else {
            // ✅ Busca apenas categorias principais
            page = categoriaRepository.findByCategoriaPaiIsNull(pageable);
        }

        // ✅ tudo é convertido dentro da sessão aberta
        return page.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<CategoriaResponse> listarTodas(Pageable pageable) {
        return categoriaRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public CategoriaResponse listarPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));
        return toResponse(categoria);
    }

    @Transactional
    public CategoriaResponse salvar(CategoriaRequest categoriaRequest) {
        categoriaRepository.findByNomeIgnoreCase(categoriaRequest.nome())
                .ifPresent(c -> { throw new ConflictException("Categoria já existe"); });

        Categoria categoria = new Categoria();
        categoria.setNome(categoriaRequest.nome());
        categoria.setAtivo(categoriaRequest.ativo());

        if (categoriaRequest.subcategorias() != null && !categoriaRequest.subcategorias().isEmpty()) {
            categoria.setSubcategorias(
                    categoriaRequest.subcategorias().stream()
                            .map(sub -> toSubcategoriaEntity(sub, categoria))
                            .collect(Collectors.toList())
            );
        }

        return toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponse atualizar(Long id, CategoriaRequest categoriaRequest) {
        Categoria existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));

        if (!existente.getNome().equalsIgnoreCase(categoriaRequest.nome())) {
            categoriaRepository.findByNomeIgnoreCase(categoriaRequest.nome())
                    .ifPresent(c -> { throw new ConflictException("Categoria já existe"); });
        }

        existente.setNome(categoriaRequest.nome());
        existente.setAtivo(categoriaRequest.ativo());

        existente.getSubcategorias().clear();
        if (categoriaRequest.subcategorias() != null && !categoriaRequest.subcategorias().isEmpty()) {
            existente.setSubcategorias(
                    categoriaRequest.subcategorias().stream()
                            .map(sub -> toSubcategoriaEntity(sub, existente))
                            .collect(Collectors.toList())
            );
        }

        return toResponse(categoriaRepository.save(existente));
    }

    @Transactional
    public CategoriaResponse desativar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));
        categoria.setAtivo(false);
        return toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public void deletar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));
        categoriaRepository.delete(categoria);
    }

    // ✅ conversão recursiva
    private CategoriaResponse toResponse(Categoria categoria) {
        List<CategoriaResponse> subcategoriasResponse = categoria.getSubcategorias()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getAtivo(),
                categoria.getCriadoEm(),
                categoria.getAtualizadoEm(),
                subcategoriasResponse
        );
    }

    private Categoria toSubcategoriaEntity(SubcategoriaRequest req, Categoria pai) {
        Categoria sub = new Categoria();
        sub.setNome(req.nome());
        sub.setAtivo(req.ativo());
        sub.setCategoriaPai(pai);

        if (req.subcategorias() != null && !req.subcategorias().isEmpty()) {
            sub.setSubcategorias(
                    req.subcategorias().stream()
                            .map(subReq -> toSubcategoriaEntity(subReq, sub))
                            .collect(Collectors.toList())
            );
        }

        return sub;
    }
}
