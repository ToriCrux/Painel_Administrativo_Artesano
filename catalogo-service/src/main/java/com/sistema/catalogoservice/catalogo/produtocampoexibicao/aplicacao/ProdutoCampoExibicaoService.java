package com.sistema.catalogoservice.catalogo.produtocampoexibicao.aplicacao;

import com.sistema.catalogoservice.catalogo.produtocampoexibicao.api.dto.*;
import com.sistema.catalogoservice.catalogo.produtocampoexibicao.dominio.CampoExibicaoPadrao;
import com.sistema.catalogoservice.catalogo.produtocampoexibicao.dominio.ProdutoCampoExibicao;
import com.sistema.catalogoservice.catalogo.produtocampoexibicao.dominio.ProdutoExibicaoExclusao;
import com.sistema.catalogoservice.catalogo.produtocampoexibicao.dominio.TipoExclusaoExibicao;
import com.sistema.catalogoservice.catalogo.produtocampoexibicao.infra.CampoExibicaoPadraoRepository;
import com.sistema.catalogoservice.catalogo.produtocampoexibicao.infra.ProdutoCampoExibicaoRepository;
import com.sistema.catalogoservice.catalogo.produtocampoexibicao.infra.ProdutoExibicaoExclusaoRepository;
import com.sistema.catalogoservice.catalogo.produto.infra.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdutoCampoExibicaoService {

    private final ProdutoCampoExibicaoRepository repository;
    private final CampoExibicaoPadraoRepository campoExibicaoPadraoRepository;
    private final ProdutoRepository produtoRepository;

    private final ProdutoExibicaoExclusaoRepository exclusaoRepository;

    // ======================================================
    // LISTAGEM (default + override)
    // ======================================================
    @Transactional(readOnly = true)
    public List<ProdutoCampoExibicaoResponse> listarConfiguracoes() {
        Map<String, Boolean> defaultCampos = getDefaultCampos();
        List<Long> produtos = produtoRepository.findAllIdsAtivos();

        return produtos.stream()
                .map(produtoId -> {
                    Map<String, Boolean> overrides = repository.findByProdutoId(produtoId)
                            .stream()
                            .collect(Collectors.toMap(
                                    ProdutoCampoExibicao::getCampo,
                                    ProdutoCampoExibicao::getVisivel,
                                    (a, b) -> b,
                                    LinkedHashMap::new
                            ));

                    Map<String, Boolean> merged = new LinkedHashMap<>(defaultCampos);
                    merged.putAll(overrides);

                    return new ProdutoCampoExibicaoResponse(produtoId, merged);
                })
                .toList();
    }

    // ======================================================
    // GET 1 PRODUTO (config + exclusões)
    // ======================================================
    @Transactional(readOnly = true)
    public ProdutoExibicaoDetalhadaResponse buscarExibicaoProduto(Long produtoId) {
        validarProdutoExiste(produtoId);

        Map<String, Boolean> mergedCampos = getMergedCampos(produtoId);
        ProdutoExibicaoExclusoesResponse exclusoes = getExclusoes(produtoId);

        return new ProdutoExibicaoDetalhadaResponse(produtoId, mergedCampos, exclusoes);
    }

    // ======================================================
    // PUT CAMPOS (override)
    // - se valor == default => remove override
    // ======================================================
    @Transactional
    public ProdutoCampoExibicaoResponse atualizarConfiguracoes(Long produtoId, ProdutoCampoExibicaoRequest request) {
        validarProdutoExiste(produtoId);

        Map<String, Boolean> defaultCampos = getDefaultCampos();
        Map<String, Boolean> incoming = (request == null || request.campos() == null) ? Map.of() : request.campos();

        for (Map.Entry<String, Boolean> entry : incoming.entrySet()) {
            String campo = entry.getKey();
            Boolean visivel = entry.getValue();

            if (campo == null || campo.isBlank() || visivel == null) continue;

            boolean defaultValue = defaultCampos.getOrDefault(campo, true);

            // igual ao default => remove override
            if (visivel.equals(defaultValue)) {
                repository.deleteByProdutoIdAndCampo(produtoId, campo);
                continue;
            }

            // diferente do default => salva override
            repository.findByProdutoIdAndCampo(produtoId, campo)
                    .ifPresentOrElse(
                            existente -> {
                                existente.setVisivel(visivel);
                                repository.save(existente);
                            },
                            () -> repository.save(
                                    ProdutoCampoExibicao.builder()
                                            .produtoId(produtoId)
                                            .campo(campo)
                                            .visivel(visivel)
                                            .build()
                            )
                    );
        }

        Map<String, Boolean> merged = getMergedCampos(produtoId);
        return new ProdutoCampoExibicaoResponse(produtoId, merged);
    }

    // ======================================================
    // PUT EXCLUSÕES (substitui tudo)
    // ======================================================
    @Transactional
    public ProdutoExibicaoExclusoesResponse atualizarExclusoes(Long produtoId, ProdutoExibicaoExclusoesRequest request) {
        validarProdutoExiste(produtoId);

        exclusaoRepository.deleteByProdutoId(produtoId);

        if (request == null) {
            return getExclusoes(produtoId);
        }

        List<ProdutoExibicaoExclusao> novos = new ArrayList<>();

        addRefIds(novos, produtoId, TipoExclusaoExibicao.CATEGORIA, safe(request.categoriaIds()));
        addRefIds(novos, produtoId, TipoExclusaoExibicao.SUBCATEGORIA, safe(request.subcategoriaIds()));
        addRefIds(novos, produtoId, TipoExclusaoExibicao.ITEM, safe(request.itemIds()));
        addRefIds(novos, produtoId, TipoExclusaoExibicao.COR_GRUPO, safe(request.corGrupoIds()));
        addRefIds(novos, produtoId, TipoExclusaoExibicao.SUBCOR, safe(request.subcorIds()));
        addChaves(novos, produtoId, TipoExclusaoExibicao.DETALHE_CHAVE, safeStr(request.detalhesChaves()));

        if (!novos.isEmpty()) {
            exclusaoRepository.saveAll(novos);
        }

        return getExclusoes(produtoId);
    }

    // ======================================================
    // HELPERS
    // ======================================================
    private void validarProdutoExiste(Long produtoId) {
        if (produtoId == null || !produtoRepository.existsById(produtoId)) {
            throw new EntityNotFoundException("Produto não encontrado: " + produtoId);
        }
    }

    private Map<String, Boolean> getDefaultCampos() {
        return campoExibicaoPadraoRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        CampoExibicaoPadrao::getNome,
                        CampoExibicaoPadrao::getVisivelPadrao,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    private Map<String, Boolean> getMergedCampos(Long produtoId) {
        Map<String, Boolean> defaultCampos = getDefaultCampos();

        Map<String, Boolean> overrides = repository.findByProdutoId(produtoId)
                .stream()
                .collect(Collectors.toMap(
                        ProdutoCampoExibicao::getCampo,
                        ProdutoCampoExibicao::getVisivel,
                        (a, b) -> b,
                        LinkedHashMap::new
                ));

        Map<String, Boolean> merged = new LinkedHashMap<>(defaultCampos);
        merged.putAll(overrides);
        return merged;
    }

    private ProdutoExibicaoExclusoesResponse getExclusoes(Long produtoId) {
        List<ProdutoExibicaoExclusao> lista = exclusaoRepository.findByProdutoId(produtoId);

        List<Long> categoriaIds = new ArrayList<>();
        List<Long> subcategoriaIds = new ArrayList<>();
        List<Long> itemIds = new ArrayList<>();
        List<Long> corGrupoIds = new ArrayList<>();
        List<Long> subcorIds = new ArrayList<>();
        List<String> detalhesChaves = new ArrayList<>();

        for (ProdutoExibicaoExclusao e : lista) {
            if (e == null || e.getTipo() == null) continue;

            if (e.getTipo() == TipoExclusaoExibicao.DETALHE_CHAVE) {
                if (e.getChave() != null && !e.getChave().isBlank()) detalhesChaves.add(e.getChave());
                continue;
            }

            Long refId = e.getRefId();
            if (refId == null) continue;

            switch (e.getTipo()) {
                case CATEGORIA -> categoriaIds.add(refId);
                case SUBCATEGORIA -> subcategoriaIds.add(refId);
                case ITEM -> itemIds.add(refId);
                case COR_GRUPO -> corGrupoIds.add(refId);
                case SUBCOR -> subcorIds.add(refId);
                default -> { }
            }
        }

        return new ProdutoExibicaoExclusoesResponse(
                uniqLong(categoriaIds),
                uniqLong(subcategoriaIds),
                uniqLong(itemIds),
                uniqLong(corGrupoIds),
                uniqLong(subcorIds),
                uniqStr(detalhesChaves)
        );
    }

    private void addRefIds(List<ProdutoExibicaoExclusao> out, Long produtoId, TipoExclusaoExibicao tipo, List<Long> ids) {
        if (out == null || produtoId == null || tipo == null || ids == null) return;

        for (Long id : ids) {
            if (id == null) continue;
            out.add(ProdutoExibicaoExclusao.builder()
                    .produtoId(produtoId)
                    .tipo(tipo)
                    .refId(id)
                    .build());
        }
    }

    private void addChaves(List<ProdutoExibicaoExclusao> out, Long produtoId, TipoExclusaoExibicao tipo, List<String> chaves) {
        if (out == null || produtoId == null || tipo == null || chaves == null) return;

        for (String k : chaves) {
            if (k == null) continue;
            String key = k.trim();
            if (key.isBlank()) continue;

            out.add(ProdutoExibicaoExclusao.builder()
                    .produtoId(produtoId)
                    .tipo(tipo)
                    .chave(key)
                    .build());
        }
    }

    private List<Long> safe(List<Long> in) { return (in == null) ? List.of() : in; }
    private List<String> safeStr(List<String> in) { return (in == null) ? List.of() : in; }

    private List<Long> uniqLong(List<Long> in) {
        if (in == null) return List.of();
        return in.stream().filter(Objects::nonNull).distinct().toList();
    }

    private List<String> uniqStr(List<String> in) {
        if (in == null) return List.of();
        return in.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();
    }
}
