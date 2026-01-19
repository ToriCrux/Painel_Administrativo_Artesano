package com.sistema.catalogoservice.catalogo.produtocampoexibicao.infra;

import com.sistema.catalogoservice.catalogo.produtocampoexibicao.dominio.ProdutoExibicaoExclusao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoExibicaoExclusaoRepository extends JpaRepository<ProdutoExibicaoExclusao, Long> {

    List<ProdutoExibicaoExclusao> findByProdutoId(Long produtoId);

    void deleteByProdutoId(Long produtoId);
}
