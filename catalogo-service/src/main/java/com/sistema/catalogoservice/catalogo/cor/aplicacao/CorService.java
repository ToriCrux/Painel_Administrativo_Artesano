package com.sistema.catalogoservice.catalogo.cor.aplicacao;

import com.sistema.catalogoservice.catalogo.cor.api.dto.CorRequest;
import com.sistema.catalogoservice.catalogo.cor.api.dto.CorResponse;
import com.sistema.catalogoservice.catalogo.cor.dominio.Cor;
import com.sistema.catalogoservice.catalogo.cor.infra.CorRepository;
import com.sistema.catalogoservice.config.exception.ConflictException;
import com.sistema.catalogoservice.config.exception.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CorService {

    private final CorRepository corRepository;

    // =====================================================
    // LISTAR
    // =====================================================
    @Transactional(readOnly = true)
    public Page<CorResponse> listar(String nome, Pageable pageable) {
        Page<Cor> page = (nome != null && !nome.isBlank())
                ? corRepository.findByNomeContainingIgnoreCase(nome, pageable)
                : corRepository.findByGrupoIsNull(pageable);

        // 🔥 Garante inicialização das subcores antes do retorno
        page.forEach(cor -> {
            if (cor.getSubcores() != null) {
                cor.getSubcores().size();
            }
        });

        return page.map(this::toResponse);
    }

    // =====================================================
    // LISTAR POR ID
    // =====================================================
    @Transactional(readOnly = true)
    public CorResponse listarPorId(Long id) {
        Cor cor = corRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cor não encontrada para este id"));

        // 🔥 Força inicialização das subcores
        if (cor.getSubcores() != null) {
            cor.getSubcores().size();
        }

        return toResponse(cor);
    }

    // =====================================================
    // SALVAR (suporta criar grupo e subcores de uma vez)
    // =====================================================
    @Transactional
    public CorResponse salvar(CorRequest request) {
        // ✅ Verifica duplicidade apenas dentro do mesmo nível (grupo raiz)
        corRepository.findByNomeIgnoreCase(request.nome())
                .filter(c -> c.getGrupo() == null)
                .ifPresent(c -> {
                    throw new ConflictException("Cor ou grupo já existente no nível raiz: " + request.nome());
                });

        Cor cor = toEntity(request, null);
        Cor salva = corRepository.save(cor);
        return toResponse(salva);
    }

    // =====================================================
    // ATUALIZAR
    // =====================================================
    @Transactional
    public CorResponse atualizar(Long id, CorRequest request) {
        Cor existente = corRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cor não encontrada para este id."));

        if (!existente.getNome().equalsIgnoreCase(request.nome())) {
            corRepository.findByNomeIgnoreCase(request.nome())
                    .filter(c -> c.getGrupo() == null)
                    .ifPresent(c -> {
                        throw new ConflictException("Nome já em uso no nível raiz: " + request.nome());
                    });
        }

        existente.setNome(request.nome());
        existente.setHex(request.hex());
        existente.setAtivo(request.ativo());

        existente.getSubcores().clear();
        if (request.subcores() != null && !request.subcores().isEmpty()) {
            existente.setSubcores(
                    request.subcores().stream()
                            .map(sub -> toEntity(sub, existente))
                            .collect(Collectors.toList())
            );
        }

        return toResponse(corRepository.save(existente));
    }

    // =====================================================
    // DESATIVAR / DELETAR
    // =====================================================
    @Transactional
    public CorResponse desativar(Long id) {
        Cor cor = corRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cor não encontrada"));
        cor.setAtivo(false);
        return toResponse(corRepository.save(cor));
    }

    @Transactional
    public void deletar(Long id) {
        Cor cor = corRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cor não encontrada"));
        corRepository.delete(cor);
    }

    // =====================================================
    // HELPERS (recursividade e DTOs)
    // =====================================================
    private Cor toEntity(CorRequest req, Cor grupo) {
        var existente = corRepository.findByNomeIgnoreCase(req.nome())
                .filter(c -> (c.getGrupo() == null && grupo == null)
                        || (c.getGrupo() != null && grupo != null && c.getGrupo().getId().equals(grupo.getId())))
                .orElse(null);

        if (existente != null) {
            return existente;
        }

        Cor cor = new Cor();
        cor.setNome(req.nome());
        cor.setHex(req.hex());
        cor.setAtivo(req.ativo() != null ? req.ativo() : true);
        cor.setGrupo(grupo);

        if (req.subcores() != null && !req.subcores().isEmpty()) {
            cor.setSubcores(
                    req.subcores().stream()
                            .map(subReq -> toEntity(subReq, cor))
                            .collect(Collectors.toList())
            );
        }
        return cor;
    }

    private CorResponse toResponse(Cor cor) {
        // 🔥 Força carregamento da coleção antes de mapear
        if (cor.getSubcores() != null) {
            cor.getSubcores().size();
        }

        List<CorResponse> subcores = cor.getSubcores() != null
                ? cor.getSubcores().stream()
                .map(this::toResponse)
                .collect(Collectors.toList())
                : List.of();

        return new CorResponse(
                cor.getId(),
                cor.getNome(),
                cor.getHex(),
                cor.getAtivo(),
                cor.getCriadoEm(),
                cor.getAtualizadoEm(),
                subcores
        );
    }
}
