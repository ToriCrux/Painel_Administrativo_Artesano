package com.sistema.catalogoservice.catalogo.catalogopublico.aplicacao;

import com.sistema.catalogoservice.catalogo.catalogopublico.dto.CorFiltroPublicoResponse;
import com.sistema.catalogoservice.catalogo.cor.dominio.Cor;
import com.sistema.catalogoservice.catalogo.cor.infra.CorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogoCorPublicaService {

    private final CorRepository corRepository;

    @Transactional(readOnly = true)
    public List<CorFiltroPublicoResponse> listarCoresAtivasParaFiltro() {

        List<Cor> grupos = corRepository.findGruposAtivosComSubcores();

        // ✅ Retorna TUDO que for ativo (sem remover vazios)
        return grupos.stream()
                .sorted(Comparator.comparing(Cor::getNome, String.CASE_INSENSITIVE_ORDER))
                .map(this::toResponseSomenteAtivos)
                .toList();
    }

    private CorFiltroPublicoResponse toResponseSomenteAtivos(Cor grupo) {

        var subcores = grupo.getSubcores().stream()
                .filter(s -> Boolean.TRUE.equals(s.getAtivo()))
                .sorted(Comparator.comparing(Cor::getNome, String.CASE_INSENSITIVE_ORDER))
                .map(s -> new CorFiltroPublicoResponse.SubcorFiltroPublicoResponse(
                        s.getId(),
                        s.getNome(),
                        s.getHex()
                ))
                .toList();

        return new CorFiltroPublicoResponse(
                grupo.getId(),
                grupo.getNome(),
                grupo.getHex(),
                subcores
        );
    }
}
