package com.sistema.catalogoservice.catalogo.produto.aplicacao;

import com.sistema.catalogoservice.catalogo.categoria.dominio.ItemCategoria;
import com.sistema.catalogoservice.catalogo.categoria.infra.ItemCategoriaRepository;
import com.sistema.catalogoservice.catalogo.cor.dominio.Cor;
import com.sistema.catalogoservice.catalogo.cor.infra.CorRepository;
import com.sistema.catalogoservice.catalogo.produto.api.dto.*;
import com.sistema.catalogoservice.catalogo.produto.dominio.Produto;
import com.sistema.catalogoservice.catalogo.produto.infra.ProdutoRepository;
import com.sistema.catalogoservice.catalogo.produtoimagem.infra.ProdutoImagemRepository;
import com.sistema.catalogoservice.config.exception.ConflictException;
import com.sistema.catalogoservice.config.exception.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ItemCategoriaRepository itemCategoriaRepository;
    private final CorRepository corRepository;
    private final ProdutoImagemRepository produtoImagemRepository;

    // ======================================================
    // ==================== LISTAGEM =========================
    // ======================================================

    @Transactional(readOnly = true)
    public Page<ProdutoResponse> listar(String nome, Pageable pageable) {
        var page = (nome != null && !nome.isBlank())
                ? produtoRepository.findByNomeContainingIgnoreCase(nome, pageable)
                : produtoRepository.findAll(pageable);

        return page.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProdutoResponse listarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado para este id."));
        return toResponse(produto);
    }

    // ======================================================
    // ==================== CRIAÇÃO ==========================
    // ======================================================

    @Transactional
    public ProdutoResponse salvar(ProdutoRequest request) {
        produtoRepository.findByCodigoIgnoreCase(request.codigo())
                .ifPresent(p -> {
                    throw new ConflictException("Código de produto já existe!");
                });

        Set<ItemCategoria> itens = resolveItensCategoria(request);
        Set<Cor> cores = resolveCoresPorGrupos(request.cores());

        Produto produto = Produto.builder()
                .codigo(request.codigo())
                .nome(request.nome())
                .itensCategoria(itens)
                .cores(cores)
                .medidas(request.medidas())
                .precoUnitario(request.precoUnitario())
                .ativo(request.ativo())
                .descricao(request.descricao())
                .build();

        Produto salvo = produtoRepository.save(produto);
        return toResponse(salvo);
    }

    // ======================================================
    // ==================== ATUALIZAÇÃO ======================
    // ======================================================

    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        if (!produto.getCodigo().equalsIgnoreCase(request.codigo())) {
            produtoRepository.findByCodigoIgnoreCase(request.codigo())
                    .ifPresent(p -> {
                        throw new ConflictException("Código já existe");
                    });
        }

        produto.setCodigo(request.codigo());
        produto.setNome(request.nome());
        produto.setMedidas(request.medidas());
        produto.setPrecoUnitario(request.precoUnitario());
        produto.setAtivo(request.ativo());
        produto.setDescricao(request.descricao());

        // ✅ agora resolve múltiplas cores e subcores por grupo
        produto.setCores(resolveCoresPorGrupos(request.cores()));

        if (request.categorias() != null && !request.categorias().isEmpty()) {
            produto.getItensCategoria().clear();
            Set<ItemCategoria> novosItens = resolveItensCategoria(request);
            produto.getItensCategoria().addAll(novosItens);
        }

        Produto atualizado = produtoRepository.save(produto);
        return toResponse(atualizado);
    }

    // ======================================================
    // ==================== DESATIVAR ========================
    // ======================================================

    @Transactional
    public ProdutoResponse desativar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        produto.setAtivo(false);
        return toResponse(produtoRepository.save(produto));
    }

    // ======================================================
    // ==================== DELETAR ==========================
    // ======================================================

    @Transactional
    public void deletar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        produtoRepository.delete(produto);
    }

    // ======================================================
    // ==================== HELPERS ==========================
    // ======================================================

    private Set<ItemCategoria> resolveItensCategoria(ProdutoRequest request) {
        Set<ItemCategoria> itens = new HashSet<>();

        for (var categoria : request.categorias()) {
            for (var sub : categoria.subcategorias()) {
                for (var itemNome : sub.itens()) {
                    ItemCategoria item = itemCategoriaRepository.findByHierarquia(
                            categoria.categoriaNome(),
                            sub.subcategoriaNome(),
                            itemNome
                    ).orElseThrow(() -> new NotFoundException(
                            "Categoria/Subcategoria/Item não encontrados: "
                                    + categoria.categoriaNome() + " / " + sub.subcategoriaNome() + " / " + itemNome
                    ));
                    itens.add(item);
                }
            }
        }
        return itens;
    }

    private ProdutoResponse toResponse(Produto produto) {
        var principalUrl = buildImagemPrincipalUrl(produto.getId());

        List<CategoriaHierarquiaResponse> categoriasResponse = produto.getItensCategoria().stream()
                .collect(Collectors.groupingBy(item -> item.getSubcategoria().getCategoria()))
                .entrySet().stream()
                .map(catEntry -> {
                    var categoria = catEntry.getKey();

                    List<SubcategoriaHierarquiaResponse> subcategorias = catEntry.getValue().stream()
                            .collect(Collectors.groupingBy(ItemCategoria::getSubcategoria))
                            .entrySet().stream()
                            .map(subEntry -> {
                                var sub = subEntry.getKey();
                                List<ItemHierarquiaResponse> itens = subEntry.getValue().stream()
                                        .map(i -> new ItemHierarquiaResponse(i.getId(), i.getNome()))
                                        .toList();

                                return new SubcategoriaHierarquiaResponse(sub.getId(), sub.getNome(), itens);
                            })
                            .toList();

                    return new CategoriaHierarquiaResponse(categoria.getId(), categoria.getNome(), subcategorias);
                })
                .toList();

        // ✅ Agrupa cores em hierarquia (grupo + subcores)
        List<CorHierarquiaResponse> coresHierarquia = produto.getCores().stream()
                .filter(c -> c.getGrupo() == null)
                .map(grupo -> {
                    List<SubcorResponse> subcores = produto.getCores().stream()
                            .filter(sub -> sub.getGrupo() != null && sub.getGrupo().getId().equals(grupo.getId()))
                            .map(sub -> new SubcorResponse(sub.getId(), sub.getNome(), sub.getHex()))
                            .toList();

                    return new CorHierarquiaResponse(grupo.getId(), grupo.getNome(), grupo.getHex(), subcores);
                })
                .toList();

        return new ProdutoResponse(
                produto.getId(),
                produto.getCodigo(),
                produto.getNome(),
                categoriasResponse,
                coresHierarquia,
                produto.getMedidas(),
                produto.getPrecoUnitario(),
                produto.getAtivo(),
                principalUrl,
                produto.getDescricao(),
                produto.getCriadoEm(),
                produto.getAtualizadoEm()
        );
    }

    /**
     * ✅ Resolve N grupos de cor, cada um com N subcores.
     * - Adiciona o grupo (somente se for grupo: grupo == null)
     * - Adiciona subcores somente se pertencerem ao grupo
     * - Valida subcores faltantes
     */
    private Set<Cor> resolveCoresPorGrupos(List<CorGrupoRequest> gruposRequest) {
        if (gruposRequest == null || gruposRequest.isEmpty()) return Collections.emptySet();

        Set<Cor> resultado = new HashSet<>();

        for (CorGrupoRequest grpReq : gruposRequest) {
            if (grpReq == null || grpReq.corNome() == null || grpReq.corNome().isBlank()) continue;

            // ✅ garante que estamos pegando um "grupo" de cor (grupo_id null)
            Cor grupo = corRepository.findByGrupoIsNullAndNomeIgnoreCase(grpReq.corNome())
                    .orElseThrow(() -> new NotFoundException("Cor/grupo não encontrado: " + grpReq.corNome()));

            resultado.add(grupo);

            List<String> subNomes = grpReq.subcorNomes();
            if (subNomes == null || subNomes.isEmpty()) continue;

            // ✅ normaliza para o formato esperado pelo repository (nomesLower)
            List<String> nomesLower = subNomes.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .map(String::toLowerCase)
                    .toList();

            // ✅ Busca somente subcores daquele grupo (sem varrer findAll)
            List<Cor> subcores = corRepository.findSubcoresDoGrupoPorNomes(grupo.getId(), nomesLower);

            // valida se todas as subcores pedidas foram encontradas
            Set<String> encontradasLower = subcores.stream()
                    .map(c -> c.getNome().toLowerCase())
                    .collect(Collectors.toSet());

            List<String> faltando = subNomes.stream()
                    .filter(n -> n != null && !n.isBlank())
                    .filter(n -> !encontradasLower.contains(n.trim().toLowerCase()))
                    .toList();

            if (!faltando.isEmpty()) {
                throw new NotFoundException("Subcores não encontradas (ou não pertencem ao grupo '"
                        + grupo.getNome() + "'): " + faltando);
            }

            resultado.addAll(subcores);
        }

        return resultado;
    }

    private String buildImagemPrincipalUrl(Long produtoId) {
        return produtoImagemRepository.findFirstByProdutoIdAndPrincipalTrue(produtoId)
                .map(img -> "/api/v1/produtos/" + produtoId + "/imagens/" + img.getId())
                .orElse(null);
    }
}
