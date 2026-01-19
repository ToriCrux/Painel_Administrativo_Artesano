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
import com.sistema.catalogoservice.mensageria.ProdutoCriadoPublisher;
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
    private final ProdutoCriadoPublisher produtoCriadoPublisher;

    // ======================================================
    // LISTAGEM
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
    // CRIAÇÃO
    // ======================================================
    @Transactional
    public ProdutoResponse salvar(ProdutoRequest request) {
        produtoRepository.findByCodigoIgnoreCase(request.codigo())
                .ifPresent(p -> { throw new ConflictException("Código de produto já existe!"); });

        Set<ItemCategoria> itens = resolveItensCategoria(request);       // ← usa request.categorias()
        Set<Cor> cores = resolveCoresPorGrupos(request.cores());        // ← pode ser null

        Produto produto = Produto.builder()
                .codigo(request.codigo())
                .nome(request.nome())
                .itensCategoria(itens)
                .cores(cores)
                .detalhesTecnicos(sanitizeDetalhes(request.detalhesTecnicos()))
                .precoUnitario(request.precoUnitario())
                .ativo(request.ativo())
                .descricao(request.descricao())
                .build();

        Produto salvo = produtoRepository.save(produto);
        produtoCriadoPublisher.publicarDepoisDoCommit(salvo);
        return toResponse(salvo);
    }

    // ======================================================
    // ATUALIZAÇÃO
    // ======================================================
    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        if (!produto.getCodigo().equalsIgnoreCase(request.codigo())) {
            produtoRepository.findByCodigoIgnoreCase(request.codigo())
                    .ifPresent(p -> { throw new ConflictException("Código já existe"); });
        }

        produto.setCodigo(request.codigo());
        produto.setNome(request.nome());
        produto.setPrecoUnitario(request.precoUnitario());
        produto.setAtivo(request.ativo());
        produto.setDescricao(request.descricao());

        // ✅ Detalhes técnicos:
        // - null => não altera
        // - {} => limpa
        if (request.detalhesTecnicos() != null) {
            produto.getDetalhesTecnicos().clear();
            produto.getDetalhesTecnicos().putAll(sanitizeDetalhes(request.detalhesTecnicos()));
        }

        // ✅ CORES:
        // - null => não altera
        // - []   => limpa
        // - [...]=> substitui
        if (request.cores() != null) {
            Set<Cor> novasCores = resolveCoresPorGrupos(request.cores()); // [] -> emptySet
            produto.getCores().clear();
            produto.getCores().addAll(novasCores);
        }

        // ✅ CATEGORIAS:
        // Como seu DTO tem @NotNull, aqui SEMPRE vem lista ([]) ou ([...]).
        // [] => limpa / [...] => substitui
        Set<ItemCategoria> novosItens = resolveItensCategoria(request); // se vier [], retorna vazio
        produto.getItensCategoria().clear();
        produto.getItensCategoria().addAll(novosItens);

        Produto atualizado = produtoRepository.save(produto);
        return toResponse(atualizado);
    }

    // ======================================================
    // DESATIVAR / DELETAR
    // ======================================================
    @Transactional
    public ProdutoResponse desativar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        produto.setAtivo(false);
        return toResponse(produtoRepository.save(produto));
    }

    @Transactional
    public void deletar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        produtoRepository.delete(produto);
    }

    // ======================================================
    // HELPERS
    // ======================================================
    private Set<ItemCategoria> resolveItensCategoria(ProdutoRequest request) {
        // ✅ Se vier vazio, retorna vazio => "limpa"
        if (request.categorias() == null || request.categorias().isEmpty()) {
            return Collections.emptySet();
        }

        Set<ItemCategoria> itens = new HashSet<>();

        for (var categoria : request.categorias()) {
            if (categoria == null) continue;

            var subcats = categoria.subcategorias();
            if (subcats == null) continue;

            for (var sub : subcats) {
                if (sub == null) continue;

                var itensNomes = sub.itens();
                if (itensNomes == null) continue;

                for (var itemNome : itensNomes) {
                    if (itemNome == null || itemNome.isBlank()) continue;

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

    private Set<Cor> resolveCoresPorGrupos(List<CorGrupoRequest> gruposRequest) {
        if (gruposRequest == null || gruposRequest.isEmpty()) return Collections.emptySet();

        Set<Cor> resultado = new HashSet<>();

        for (CorGrupoRequest grpReq : gruposRequest) {
            if (grpReq == null || grpReq.corNome() == null || grpReq.corNome().isBlank()) continue;

            Cor grupo = corRepository.findByGrupoIsNullAndNomeIgnoreCase(grpReq.corNome())
                    .orElseThrow(() -> new NotFoundException("Cor/grupo não encontrado: " + grpReq.corNome()));

            resultado.add(grupo);

            List<String> subNomes = grpReq.subcorNomes();
            if (subNomes == null || subNomes.isEmpty()) continue;

            List<String> nomesLower = subNomes.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .map(String::toLowerCase)
                    .toList();

            List<Cor> subcores = corRepository.findSubcoresDoGrupoPorNomes(grupo.getId(), nomesLower);

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

    private Map<String, String> sanitizeDetalhes(Map<String, String> incoming) {
        if (incoming == null || incoming.isEmpty()) return new LinkedHashMap<>();

        LinkedHashMap<String, String> out = new LinkedHashMap<>();
        incoming.forEach((k, v) -> {
            String key = (k == null) ? "" : k.trim();
            String val = (v == null) ? "" : v.trim();
            if (!key.isBlank()) out.put(key, val);
        });
        return out;
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
                produto.getDetalhesTecnicos() == null ? Map.of() : produto.getDetalhesTecnicos(),
                produto.getPrecoUnitario(),
                produto.getAtivo(),
                principalUrl,
                produto.getDescricao(),
                produto.getCriadoEm(),
                produto.getAtualizadoEm()
        );
    }

    private String buildImagemPrincipalUrl(Long produtoId) {
        return produtoImagemRepository.findFirstByProdutoIdAndPrincipalTrue(produtoId)
                .map(img -> "/api/v1/produtos/" + produtoId + "/imagens/" + img.getId())
                .orElse(null);
    }
}
