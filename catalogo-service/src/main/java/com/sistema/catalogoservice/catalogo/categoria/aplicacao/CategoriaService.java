package com.sistema.catalogoservice.catalogo.categoria.aplicacao;

import com.sistema.catalogoservice.catalogo.categoria.api.dto.*;
import com.sistema.catalogoservice.catalogo.categoria.dominio.*;
import com.sistema.catalogoservice.catalogo.categoria.infra.*;
import com.sistema.catalogoservice.config.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional
    public CategoriaResponse salvar(CategoriaRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNome(request.nome());
        categoria.setAtivo(request.ativo());

        if (request.subcategorias() != null) {
            categoria.setSubcategorias(request.subcategorias().stream()
                    .map(subReq -> mapSubcategoria(subReq, categoria))
                    .collect(Collectors.toList()));
        }

        return toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponse atualizar(Long id, CategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));

        // Atualiza dados principais da categoria
        categoria.setNome(request.nome());
        categoria.setAtivo(request.ativo());

        // Atualiza subcategorias
        if (request.subcategorias() != null) {
            // Para cada subcategoria no request
            for (SubcategoriaRequest subReq : request.subcategorias()) {
                // Tenta encontrar uma subcategoria existente com o mesmo nome
                Subcategoria existente = categoria.getSubcategorias().stream()
                        .filter(s -> s.getNome().equalsIgnoreCase(subReq.nome()))
                        .findFirst()
                        .orElse(null);

                if (existente != null) {
                    // Atualiza subcategoria existente
                    existente.setAtivo(subReq.ativo());

                    // Atualiza itens dessa subcategoria
                    if (subReq.itens() != null) {
                        for (ItemCategoriaRequest itemReq : subReq.itens()) {
                            ItemCategoria itemExistente = existente.getItens().stream()
                                    .filter(i -> i.getNome().equalsIgnoreCase(itemReq.nome()))
                                    .findFirst()
                                    .orElse(null);

                            if (itemExistente != null) {
                                itemExistente.setAtivo(itemReq.ativo());
                            } else {
                                // Adiciona novo item se não existir
                                ItemCategoria novoItem = new ItemCategoria();
                                novoItem.setNome(itemReq.nome());
                                novoItem.setAtivo(itemReq.ativo());
                                novoItem.setSubcategoria(existente);
                                existente.getItens().add(novoItem);
                            }
                        }

                        // Remove itens que não estão mais no request
                        existente.getItens().removeIf(
                                item -> subReq.itens().stream()
                                        .noneMatch(reqItem -> reqItem.nome().equalsIgnoreCase(item.getNome()))
                        );
                    }
                } else {
                    // Cria nova subcategoria se não existir
                    Subcategoria novaSub = new Subcategoria();
                    novaSub.setNome(subReq.nome());
                    novaSub.setAtivo(subReq.ativo());
                    novaSub.setCategoria(categoria);

                    if (subReq.itens() != null) {
                        novaSub.setItens(subReq.itens().stream()
                                .map(i -> {
                                    ItemCategoria novoItem = new ItemCategoria();
                                    novoItem.setNome(i.nome());
                                    novoItem.setAtivo(i.ativo());
                                    novoItem.setSubcategoria(novaSub);
                                    return novoItem;
                                })
                                .collect(Collectors.toList()));
                    }

                    categoria.getSubcategorias().add(novaSub);
                }
            }

            // Remove subcategorias que não estão mais no request
            categoria.getSubcategorias().removeIf(
                    sub -> request.subcategorias().stream()
                            .noneMatch(reqSub -> reqSub.nome().equalsIgnoreCase(sub.getNome()))
            );
        }

        Categoria atualizada = categoriaRepository.save(categoria);
        return toResponse(atualizada);
    }

    @Transactional
    public void deletar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));
        categoriaRepository.delete(categoria);
    }

    @Transactional(readOnly = true)
    public CategoriaResponse listarPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));
        return toResponse(categoria);
    }

    // ✅ NOVO MÉTODO PAGINADO USANDO DTO CUSTOMIZADO
    @Transactional(readOnly = true)
    public PaginacaoResponse<CategoriaResponse> listarPaginado(int page, int size, String sort, String direction) {
        Sort sortOrder = direction.equalsIgnoreCase("desc")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(page, size, sortOrder);
        Page<Categoria> result = categoriaRepository.findAll(pageable);

        return new PaginacaoResponse<>(
                result.map(this::toResponse).getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast()
        );
    }

    // ======== MAPEAMENTO AUXILIAR ========

    private Subcategoria mapSubcategoria(SubcategoriaRequest req, Categoria categoria) {
        Subcategoria sub = new Subcategoria();
        sub.setNome(req.nome());
        sub.setAtivo(req.ativo());
        sub.setCategoria(categoria);

        if (req.itens() != null) {
            sub.setItens(req.itens().stream()
                    .map(i -> mapItem(i, sub))
                    .collect(Collectors.toList()));
        }
        return sub;
    }

    private ItemCategoria mapItem(ItemCategoriaRequest req, Subcategoria sub) {
        ItemCategoria item = new ItemCategoria();
        item.setNome(req.nome());
        item.setAtivo(req.ativo());
        item.setSubcategoria(sub);
        return item;
    }

    // ======== CONVERSÕES PARA RESPONSE ========

    private CategoriaResponse toResponse(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getAtivo(),
                categoria.getCriadoEm(),
                categoria.getAtualizadoEm(),
                categoria.getSubcategorias().stream().map(this::toResponse).collect(Collectors.toList())
        );
    }

    private SubcategoriaResponse toResponse(Subcategoria sub) {
        return new SubcategoriaResponse(
                sub.getId(),
                sub.getNome(),
                sub.getAtivo(),
                sub.getCriadoEm(),
                sub.getAtualizadoEm(),
                sub.getItens().stream().map(this::toResponse).collect(Collectors.toList())
        );
    }

    private ItemCategoriaResponse toResponse(ItemCategoria item) {
        return new ItemCategoriaResponse(
                item.getId(),
                item.getNome(),
                item.getAtivo(),
                item.getCriadoEm(),
                item.getAtualizadoEm()
        );
    }
}
