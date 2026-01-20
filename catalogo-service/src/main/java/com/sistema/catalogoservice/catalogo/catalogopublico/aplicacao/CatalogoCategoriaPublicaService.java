package com.sistema.catalogoservice.catalogo.catalogopublico.aplicacao;

import com.sistema.catalogoservice.catalogo.catalogopublico.dto.CategoriaFiltroPublicoResponse;
import com.sistema.catalogoservice.catalogo.categoria.dominio.Categoria;
import com.sistema.catalogoservice.catalogo.categoria.dominio.Subcategoria;
import com.sistema.catalogoservice.catalogo.categoria.infra.CategoriaRepository;
import com.sistema.catalogoservice.catalogo.categoria.infra.SubcategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogoCategoriaPublicaService {

    private final CategoriaRepository categoriaRepository;
    private final SubcategoriaRepository subcategoriaRepository;

    @Transactional(readOnly = true)
    public List<CategoriaFiltroPublicoResponse> listarCategoriasAtivasParaFiltro() {

        // ✅ 1) categorias ativas + subcategorias
        List<Categoria> categorias = categoriaRepository.findAllAtivasComSubcategorias();

        // ✅ 2) inicializa itens de todas as subcategorias dessas categorias
        var categoriaIds = categorias.stream().map(Categoria::getId).toList();
        if (!categoriaIds.isEmpty()) {
            subcategoriaRepository.findAllByCategoriaIdInFetchItens(categoriaIds);
        }

        // ✅ Retorna TUDO que for ativo (sem remover vazios)
        return categorias.stream()
                .sorted(Comparator.comparing(Categoria::getNome, String.CASE_INSENSITIVE_ORDER))
                .map(this::toResponseSomenteAtivos)
                .toList();
    }

    private CategoriaFiltroPublicoResponse toResponseSomenteAtivos(Categoria c) {

        var subcats = c.getSubcategorias().stream()
                .filter(s -> Boolean.TRUE.equals(s.getAtivo()))
                .sorted(Comparator.comparing(Subcategoria::getNome, String.CASE_INSENSITIVE_ORDER))
                .map(s -> new CategoriaFiltroPublicoResponse.SubcategoriaFiltroPublicoResponse(
                        s.getId(),
                        s.getNome(),
                        s.getItens().stream()
                                .filter(i -> Boolean.TRUE.equals(i.getAtivo()))
                                .sorted(Comparator.comparing(i -> i.getNome(), String.CASE_INSENSITIVE_ORDER))
                                .map(i -> new CategoriaFiltroPublicoResponse.ItemFiltroPublicoResponse(
                                        i.getId(),
                                        i.getNome()
                                ))
                                .toList()
                ))
                .toList();

        return new CategoriaFiltroPublicoResponse(
                c.getId(),
                c.getNome(),
                subcats
        );
    }
}
