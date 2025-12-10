package com.sistema.catalogoservice.catalogo.produtocampoexibicao.aplicacao;

import com.sistema.catalogoservice.catalogo.produtocampoexibicao.api.dto.ProdutoCampoExibicaoRequest;
import com.sistema.catalogoservice.catalogo.produtocampoexibicao.api.dto.ProdutoCampoExibicaoResponse;
import com.sistema.catalogoservice.catalogo.produtocampoexibicao.dominio.CampoExibicaoPadrao;
import com.sistema.catalogoservice.catalogo.produtocampoexibicao.dominio.ProdutoCampoExibicao;
import com.sistema.catalogoservice.catalogo.produtocampoexibicao.infra.CampoExibicaoPadraoRepository;
import com.sistema.catalogoservice.catalogo.produtocampoexibicao.infra.ProdutoCampoExibicaoRepository;
import com.sistema.catalogoservice.catalogo.produto.infra.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdutoCampoExibicaoService {

    private final ProdutoCampoExibicaoRepository repository;
    private final CampoExibicaoPadraoRepository campoExibicaoPadraoRepository;
    private final ProdutoRepository produtoRepository;

    // Lista todos os produtos com suas configurações (padrão + personalizadas)
    public List<ProdutoCampoExibicaoResponse> listarConfiguracoes() {

        Map<String, Boolean> defaultCampos = campoExibicaoPadraoRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        CampoExibicaoPadrao::getNome,
                        CampoExibicaoPadrao::getVisivelPadrao
                ));

        List<Long> produtos = produtoRepository.findAllIdsAtivos();

        return produtos.stream()
                .map(produtoId -> {
                    Map<String, Boolean> configuracoes = repository.findByProdutoId(produtoId)
                            .stream()
                            .collect(Collectors.toMap(
                                    ProdutoCampoExibicao::getCampo,
                                    ProdutoCampoExibicao::getVisivel
                            ));

                    Map<String, Boolean> merged = new LinkedHashMap<>(defaultCampos);
                    merged.putAll(configuracoes);

                    return new ProdutoCampoExibicaoResponse(produtoId, merged);
                })
                .collect(Collectors.toList());
    }

    // Atualiza as configurações de um produto específico
    public ProdutoCampoExibicaoResponse atualizarConfiguracoes(Long produtoId, ProdutoCampoExibicaoRequest request) {

        request.campos().forEach((campo, visivel) -> {
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
        });

        Map<String, Boolean> defaultCampos = campoExibicaoPadraoRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        CampoExibicaoPadrao::getNome,
                        CampoExibicaoPadrao::getVisivelPadrao
                ));

        Map<String, Boolean> config = repository.findByProdutoId(produtoId)
                .stream()
                .collect(Collectors.toMap(
                        ProdutoCampoExibicao::getCampo,
                        ProdutoCampoExibicao::getVisivel
                ));

        Map<String, Boolean> merged = new LinkedHashMap<>(defaultCampos);
        merged.putAll(config);

        return new ProdutoCampoExibicaoResponse(produtoId, merged);
    }
}
