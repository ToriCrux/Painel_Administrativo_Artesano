package com.sistema.admin.catalogo.cor.aplicacao;

import com.sistema.admin.catalogo.cor.dominio.Cor;
import com.sistema.admin.catalogo.cor.infra.CorRepository;
import com.sistema.admin.catalogo.cor.api.dto.CorRequest;
import com.sistema.admin.catalogo.cor.api.dto.CorResponse;
import com.sistema.admin.config.exception.ConflictException;
import com.sistema.admin.config.exception.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CorService {

    private final CorRepository corRepository;

    public Page<CorResponse> listar(String nome, Pageable pageable) {
        Page<Cor> cores;
        if (nome != null && !nome.isBlank()) {
            cores = corRepository.findByNomeContainingIgnoreCase(nome, pageable);
        } else {
            cores = corRepository.findAll(pageable);
        }
        return cores.map(this::toResponse);
    }

    public CorResponse listarPorId(Long id) {
        Cor cor = corRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cor não encontrada para este id"));
        return toResponse(cor);
    }

    public CorResponse salvar(CorRequest corRequest) {
        // Verifica se já existe cor com o mesmo nome
        corRepository.findByNomeIgnoreCase(corRequest.nome())
                .ifPresent(c -> { throw new ConflictException("Cor existente para este nome."); });

        Cor novaCor = toEntity(corRequest);
        return toResponse(corRepository.save(novaCor));
    }

    public CorResponse atualizar(Long id, CorRequest corRequest) {
        Cor existente = corRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cor não encontrada para este id."));

        if (!existente.getNome().equalsIgnoreCase(corRequest.nome())) {
            corRepository.findByNomeIgnoreCase(corRequest.nome())
                    .ifPresent(c -> { throw new ConflictException("Cor já existe"); });
        }

        existente.setNome(corRequest.nome());
        existente.setHex(corRequest.hex());
        existente.setAtivo(corRequest.ativo());

        return toResponse(corRepository.save(existente));
    }

    public CorResponse desativar(Long id) {
        Cor cor = corRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cor não encontrada"));
        cor.setAtivo(false);
        return toResponse(corRepository.save(cor));
    }

    public void deletar(Long id) {
        Cor cor = corRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cor não encontrada"));
        corRepository.deleteById(cor.getId());
    }

    // usar @Mapper é mais DDD friendly
    private CorResponse toResponse(Cor cor) {
        return new CorResponse(
                cor.getId(),
                cor.getNome(),
                cor.getHex(),
                cor.getAtivo(),
                cor.getCriadoEm(),
                cor.getAtualizadoEm()
        );
    }

    // usar @Mapper é mais DDD friendly
    private Cor toEntity(CorRequest corRequest) {
        return Cor.builder()
                .nome(corRequest.nome())
                .hex(corRequest.hex())
                .ativo(corRequest.ativo() != null ? corRequest.ativo() : true)
                .build();
    }
}
