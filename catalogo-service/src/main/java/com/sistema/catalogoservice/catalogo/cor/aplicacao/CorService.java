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

import java.util.*;
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

        page.forEach(cor -> cor.getSubcores().size());
        return page.map(this::toResponse);
    }

    // =====================================================
    // LISTAR POR ID
    // =====================================================
    @Transactional(readOnly = true)
    public CorResponse listarPorId(Long id) {
        Cor cor = corRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cor não encontrada para este id"));
        cor.getSubcores().size();
        return toResponse(cor);
    }

    // =====================================================
    // SALVAR
    // =====================================================
    @Transactional
    public CorResponse salvar(CorRequest request) {
        if (corRepository.existsByGrupoIsNullAndNomeIgnoreCase(request.nome())) {
            throw new ConflictException("Cor ou grupo já existente no nível raiz: " + request.nome());
        }

        Cor cor = toEntity(request, null);
        Cor salva = corRepository.save(cor);
        return toResponse(salva);
    }

    // =====================================================
    // ATUALIZAR (PUT) - ✅ MERGE
    // =====================================================
    @Transactional
    public CorResponse atualizar(Long id, CorRequest request) {
        Cor existente = corRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cor não encontrada para este id."));

        // valida renomear no nível raiz
        if (!existente.getNome().equalsIgnoreCase(request.nome())) {
            if (corRepository.existsByGrupoIsNullAndNomeIgnoreCaseAndIdNot(request.nome(), id)) {
                throw new ConflictException("Nome já em uso no nível raiz: " + request.nome());
            }
        }

        existente.setNome(request.nome());
        existente.setHex(request.hex());
        existente.setAtivo(request.ativo() != null ? request.ativo() : existente.getAtivo());

        // ✅ MERGE das subcores (por id OU por nome)
        mergeSubcores(existente, request.subcores());

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
    // ✅ MERGE HELPERS
    // =====================================================

    private void mergeSubcores(Cor pai, List<CorRequest> incoming) {
        // se não vier subcores no PUT, assume que quer remover todas
        if (incoming == null) {
            pai.getSubcores().clear();
            return;
        }

        // ✅ impede duplicado no próprio payload (mesmo nome repetido)
        ensureNoDuplicateNames(incoming);

        // mapa de existentes por ID
        Map<Long, Cor> existentesPorId = pai.getSubcores().stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(Cor::getId, c -> c));

        // mapa de existentes por nome (case-insensitive) dentro do mesmo grupo
        Map<String, Cor> existentesPorNome = pai.getSubcores().stream()
                .collect(Collectors.toMap(c -> normalize(c.getNome()), c -> c, (a, b) -> a));

        // nova lista final (reaproveitando entidades gerenciadas)
        List<Cor> novaLista = new ArrayList<>();
        Set<Long> keepIds = new HashSet<>();

        for (CorRequest dto : incoming) {
            Cor alvo = null;

            // 1) se veio ID, prioriza ID
            if (dto.id() != null) {
                alvo = existentesPorId.get(dto.id());
                if (alvo == null) {
                    throw new NotFoundException("Subcor não encontrada para o id: " + dto.id());
                }
            } else {
                // 2) se não veio ID, tenta casar pelo nome dentro do grupo
                alvo = existentesPorNome.get(normalize(dto.nome()));
            }

            // 3) se não achou, cria nova
            if (alvo == null) {
                alvo = new Cor();
                alvo.setGrupo(pai);
            }

            // atualiza campos
            alvo.setNome(dto.nome());
            alvo.setHex(dto.hex());
            alvo.setAtivo(dto.ativo() != null ? dto.ativo() : true);

            // recursivo
            mergeSubcores(alvo, dto.subcores());

            novaLista.add(alvo);

            if (alvo.getId() != null) {
                keepIds.add(alvo.getId());
            }
        }

        // remove órfãs (as que existiam e não estão mais no payload)
        pai.getSubcores().removeIf(existing ->
                existing.getId() != null && !keepIds.contains(existing.getId())
        );

        // agora sincroniza a coleção mantendo referência gerenciada
        pai.getSubcores().clear();
        pai.getSubcores().addAll(novaLista);
    }

    private void ensureNoDuplicateNames(List<CorRequest> incoming) {
        Set<String> seen = new HashSet<>();
        for (CorRequest dto : incoming) {
            String key = normalize(dto.nome());
            if (!seen.add(key)) {
                throw new ConflictException("Subcores duplicadas no payload: " + dto.nome());
            }
        }
    }

    private String normalize(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
    }

    // =====================================================
    // ENTITY/RESPONSE HELPERS
    // =====================================================
    private Cor toEntity(CorRequest req, Cor grupo) {
        Cor cor = new Cor();
        cor.setNome(req.nome());
        cor.setHex(req.hex());
        cor.setAtivo(req.ativo() != null ? req.ativo() : true);
        cor.setGrupo(grupo);

        if (req.subcores() != null && !req.subcores().isEmpty()) {
            List<Cor> subs = req.subcores().stream()
                    .map(subReq -> toEntity(subReq, cor))
                    .collect(Collectors.toList());
            cor.setSubcores(subs);
        }
        return cor;
    }

    private CorResponse toResponse(Cor cor) {
        cor.getSubcores().size();

        List<CorResponse> subcores = cor.getSubcores() != null
                ? cor.getSubcores().stream().map(this::toResponse).collect(Collectors.toList())
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
