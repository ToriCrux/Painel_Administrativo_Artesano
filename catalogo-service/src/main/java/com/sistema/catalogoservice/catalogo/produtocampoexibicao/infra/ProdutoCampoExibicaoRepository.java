package com.sistema.catalogoservice.catalogo.produtocampoexibicao.infra;

import com.sistema.catalogoservice.catalogo.produtocampoexibicao.dominio.ProdutoCampoExibicao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProdutoCampoExibicaoRepository extends JpaRepository<ProdutoCampoExibicao, Long> {

    List<ProdutoCampoExibicao> findByProdutoId(Long produtoId);

    Optional<ProdutoCampoExibicao> findByProdutoIdAndCampo(Long produtoId, String campo);
}
