package com.sistema.catalogoservice.catalogo.produtocampoexibicao.infra;

import com.sistema.catalogoservice.catalogo.produtocampoexibicao.dominio.CampoExibicaoPadrao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampoExibicaoPadraoRepository extends JpaRepository<CampoExibicaoPadrao, Long> {
}
