package com.sistema.catalogoservice.catalogo.catalogopublico.aplicacao;

import com.sistema.catalogoservice.catalogo.catalogopublico.dto.CatalogoProdutoResponse;
import com.sistema.catalogoservice.catalogo.categoria.dominio.Categoria;
import com.sistema.catalogoservice.catalogo.categoria.dominio.Subcategoria;
import com.sistema.catalogoservice.catalogo.cor.dominio.Cor;
import com.sistema.catalogoservice.catalogo.produtocampoexibicao.dominio.CampoExibicaoPadrao;
import com.sistema.catalogoservice.catalogo.produtocampoexibicao.dominio.ProdutoCampoExibicao;
import com.sistema.catalogoservice.catalogo.produtocampoexibicao.infra.CampoExibicaoPadraoRepository;
import com.sistema.catalogoservice.catalogo.produtocampoexibicao.infra.ProdutoCampoExibicaoRepository;
import com.sistema.catalogoservice.catalogo.produtoimagem.infra.ProdutoImagemRepository;
import com.sistema.catalogoservice.catalogo.produto.infra.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogoPublicoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoCampoExibicaoRepository produtoCampoExibicaoRepository;
    private final CampoExibicaoPadraoRepository campoExibicaoPadraoRepository;
    private final ProdutoImagemRepository produtoImagemRepository;

    @Transactional(readOnly = true)
    public List<CatalogoProdutoResponse> listarProdutosPublicos() {

        var produtos = produtoRepository.findAll()
                .stream()
                .filter(p -> Boolean.TRUE.equals(p.getAtivo()))
                .toList();

        Map<String, Boolean> padrao = campoExibicaoPadraoRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        CampoExibicaoPadrao::getNome,
                        CampoExibicaoPadrao::getVisivelPadrao,
                        (a, b) -> b,
                        LinkedHashMap::new
                ));

        return produtos.stream().map(produto -> {

            Map<String, Boolean> custom = produtoCampoExibicaoRepository.findByProdutoId(produto.getId())
                    .stream()
                    .collect(Collectors.toMap(
                            ProdutoCampoExibicao::getCampo,
                            ProdutoCampoExibicao::getVisivel,
                            (a, b) -> b,
                            LinkedHashMap::new
                    ));

            Map<String, Boolean> merged = new LinkedHashMap<>(padrao);
            merged.putAll(custom);

            Map<String, Object> camposVisiveis = new LinkedHashMap<>();

            if (merged.getOrDefault("codigo", true)) camposVisiveis.put("codigo", produto.getCodigo());
            if (merged.getOrDefault("nome", true)) camposVisiveis.put("nome", produto.getNome());
            if (merged.getOrDefault("descricao", true)) camposVisiveis.put("descricao", produto.getDescricao());
            if (merged.getOrDefault("precoUnitario", true)) camposVisiveis.put("precoUnitario", produto.getPrecoUnitario());

            // ✅ detalhesTecnicos
            if (merged.getOrDefault("detalhesTecnicos", true)) {
                Map<String, String> detalhes = produto.getDetalhesTecnicos() == null
                        ? Map.of()
                        : new LinkedHashMap<>(produto.getDetalhesTecnicos());

                camposVisiveis.put("detalhesTecnicos", detalhes);
            }

            // ==========================
            // ✅ CATEGORIAS (CORRIGIDO: chave = "categorias")
            // ==========================
            if (merged.getOrDefault("categorias", true)) {
                try {
                    List<Map<String, Object>> categoriasInfo = produto.getItensCategoria().stream().map(item -> {
                        Map<String, Object> categoriaInfo = new LinkedHashMap<>();
                        Subcategoria sub = item.getSubcategoria();
                        Categoria cat = (sub != null) ? sub.getCategoria() : null;

                        if (cat != null) categoriaInfo.put("categoria", cat.getNome());
                        if (sub != null) categoriaInfo.put("subcategoria", sub.getNome());
                        categoriaInfo.put("item", item.getNome());

                        return categoriaInfo;
                    }).toList();

                    camposVisiveis.put("categorias", categoriasInfo);
                } catch (Exception e) {
                    camposVisiveis.put("categorias", List.of(Map.of(
                            "erro", "Categoria não disponível",
                            "detalhe", e.getClass().getSimpleName()
                    )));
                }
            }

            // ==========================
            // ✅ CORES (CORRIGIDO: chave = "cores")
            // ==========================
            if (merged.getOrDefault("cores", true)) {
                try {
                    var coresHierarquia = buildCoresHierarquia(produto.getCores());
                    camposVisiveis.put("coresHierarquia", coresHierarquia);
                } catch (Exception e) {
                    camposVisiveis.put("coresHierarquia", List.of(Map.of(
                            "erro", "Cores não disponíveis",
                            "detalhe", e.getClass().getSimpleName()
                    )));
                }
            }

            // ==========================
            // Imagens públicas
            // ==========================
            var imagens = produtoImagemRepository.findByProdutoIdOrderByPrincipalDescOrdemAscIdAsc(produto.getId());

            CatalogoProdutoResponse.ImagemPrincipal imagemPrincipal = null;
            List<CatalogoProdutoResponse.ImagemSecundaria> imagensSecundarias = List.of();

            if (imagens != null && !imagens.isEmpty()) {
                var principal = imagens.stream()
                        .filter(i -> Boolean.TRUE.equals(i.getPrincipal()))
                        .findFirst();

                if (principal.isPresent()) {
                    var img = principal.get();
                    imagemPrincipal = new CatalogoProdutoResponse.ImagemPrincipal(
                            img.getId(),
                            "/public/produtos/" + produto.getId() + "/imagens/" + img.getId(),
                            img.getContentType(),
                            img.getNomeArquivo()
                    );
                }

                imagensSecundarias = imagens.stream()
                        .filter(i -> !Boolean.TRUE.equals(i.getPrincipal()))
                        .map(i -> new CatalogoProdutoResponse.ImagemSecundaria(
                                i.getId(),
                                "/public/produtos/" + produto.getId() + "/imagens/" + i.getId(),
                                i.getContentType(),
                                i.getNomeArquivo(),
                                i.getPrincipal(),
                                i.getOrdem()
                        ))
                        .toList();
            }

            return new CatalogoProdutoResponse(
                    produto.getId(),
                    produto.getCodigo(),
                    camposVisiveis,
                    imagemPrincipal,
                    imagensSecundarias
            );
        }).toList();
    }

    private List<Map<String, Object>> buildCoresHierarquia(Set<Cor> cores) {
        if (cores == null || cores.isEmpty()) return List.of();

        List<Cor> grupos = cores.stream()
                .filter(c -> c != null && c.getGrupo() == null)
                .toList();

        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Cor grupo : grupos) {
            Map<String, Object> grp = new LinkedHashMap<>();
            grp.put("id", grupo.getId());
            grp.put("nome", grupo.getNome());
            grp.put("hex", grupo.getHex());

            List<Map<String, Object>> subcores = cores.stream()
                    .filter(sub -> sub != null
                            && sub.getGrupo() != null
                            && Objects.equals(sub.getGrupo().getId(), grupo.getId()))
                    .map(sub -> {
                        Map<String, Object> sc = new LinkedHashMap<>();
                        sc.put("id", sub.getId());
                        sc.put("nome", sub.getNome());
                        sc.put("hex", sub.getHex());
                        return sc;
                    })
                    .toList();

            grp.put("subcores", subcores);
            resultado.add(grp);
        }

        return resultado;
    }
}
