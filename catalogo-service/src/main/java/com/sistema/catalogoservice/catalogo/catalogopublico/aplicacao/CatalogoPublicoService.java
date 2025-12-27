package com.sistema.catalogoservice.catalogo.catalogopublico.aplicacao;

import com.sistema.catalogoservice.catalogo.produto.dominio.Produto;
import com.sistema.catalogoservice.catalogo.produto.infra.ProdutoRepository;
import com.sistema.catalogoservice.catalogo.produtocampoexibicao.dominio.CampoExibicaoPadrao;
import com.sistema.catalogoservice.catalogo.produtocampoexibicao.dominio.ProdutoCampoExibicao;
import com.sistema.catalogoservice.catalogo.produtocampoexibicao.infra.ProdutoCampoExibicaoRepository;
import com.sistema.catalogoservice.catalogo.produtocampoexibicao.infra.CampoExibicaoPadraoRepository;
import com.sistema.catalogoservice.catalogo.catalogopublico.dto.CatalogoProdutoResponse;
import com.sistema.catalogoservice.catalogo.produtoimagem.infra.ProdutoImagemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogoPublicoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoCampoExibicaoRepository produtoCampoExibicaoRepository;
    private final CampoExibicaoPadraoRepository campoExibicaoPadraoRepository;
    private final ProdutoImagemRepository produtoImagemRepository;

    public List<CatalogoProdutoResponse> listarProdutosPublicos() {

        var produtos = produtoRepository.findAll()
                .stream()
                .filter(Produto::getAtivo)
                .toList();

        Map<String, Boolean> padrao = campoExibicaoPadraoRepository.findAll()
                .stream()
                .collect(Collectors.toMap(CampoExibicaoPadrao::getNome, CampoExibicaoPadrao::getVisivelPadrao));

        return produtos.stream().map(produto -> {

            Map<String, Boolean> custom = produtoCampoExibicaoRepository.findByProdutoId(produto.getId())
                    .stream()
                    .collect(Collectors.toMap(ProdutoCampoExibicao::getCampo, ProdutoCampoExibicao::getVisivel));

            Map<String, Boolean> merged = new LinkedHashMap<>(padrao);
            merged.putAll(custom);

            Map<String, Object> camposVisiveis = new LinkedHashMap<>();

            if (merged.getOrDefault("codigo", true)) camposVisiveis.put("codigo", produto.getCodigo());
            if (merged.getOrDefault("nome", true)) camposVisiveis.put("nome", produto.getNome());
            if (merged.getOrDefault("descricao", true)) camposVisiveis.put("descricao", produto.getDescricao());
            if (merged.getOrDefault("precoUnitario", true)) camposVisiveis.put("precoUnitario", produto.getPrecoUnitario());
            if (merged.getOrDefault("medidas", true)) camposVisiveis.put("medidas", produto.getMedidas());
            if (merged.getOrDefault("categoria", true)) camposVisiveis.put("categoria", produto.getCategoria().getNome());

            // 🔹 Carregar imagens
            var imagens = produtoImagemRepository.findByProdutoIdOrderByPrincipalDescOrdemAscIdAsc(produto.getId());

            CatalogoProdutoResponse.ImagemPrincipal imagemPrincipal = null;
            List<CatalogoProdutoResponse.ImagemSecundaria> imagensSecundarias = List.of();

            if (!imagens.isEmpty()) {
                var principal = imagens.stream().filter(i -> Boolean.TRUE.equals(i.getPrincipal())).findFirst();
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
}
