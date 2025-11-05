package com.sistema.admin.catalogo.produtoimagem.infra;

import com.sistema.admin.catalogo.produtoimagem.dominio.ProdutoImagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProdutoImagemRepository extends JpaRepository<ProdutoImagem, Long> {

    List<ProdutoImagem> findByProdutoIdOrderByPrincipalDescOrdemAscIdAsc(Long produtoId);

    Optional<ProdutoImagem> findFirstByProdutoIdAndPrincipalTrue(Long produtoId);

    @Modifying
    @Query("update ProdutoImagem i set i.principal = false where i.produto.id = :produtoId and i.principal = true")
    void desmarcarPrincipais(@Param("produtoId") Long produtoId);
}