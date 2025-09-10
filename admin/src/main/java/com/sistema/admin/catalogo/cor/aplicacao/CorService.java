package com.sistema.admin.catalogo.cor.aplicacao;


import com.sistema.admin.catalogo.cor.dominio.Cor;
import com.sistema.admin.catalogo.cor.infra.CorRepository;
import com.sistema.admin.catalogo.cor.api.dto.CorRequest;
import com.sistema.admin.catalogo.cor.api.dto.CorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CorService {

    private final CorRepository repository;

    public Page<CorResponse> listar(String nome, Pageable pageable) {
        Page<Cor> cores;
        if (nome != null && !nome.isBlank()) {
            cores = repository.findByNomeContainingIgnoreCase(nome, pageable);
        } else {
            cores = repository.findAll(pageable);
        }
        return cores.map(this::toResponse);
    }

    public CorResponse salvar(CorRequest dto) {
        repository.findByNomeIgnoreCase(dto.nome())
                .ifPresent(c -> { throw new IllegalArgumentException("Cor já existe"); });

        Cor nova = toEntity(dto);
        Cor salva = repository.save(nova);
        return toResponse(salva);
    }

    public CorResponse atualizar(Long id, CorRequest dto) {
        Cor existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cor não encontrada"));

        if (!existente.getNome().equalsIgnoreCase(dto.nome())) {
            repository.findByNomeIgnoreCase(dto.nome())
                    .ifPresent(c -> { throw new IllegalArgumentException("Cor já existe"); });
        }

        existente.setNome(dto.nome());
        existente.setHex(dto.hex());
        existente.setAtivo(dto.ativo());

        Cor atualizado = repository.save(existente);
        return toResponse(atualizado);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

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

    private Cor toEntity(CorRequest dto) {
        return Cor.builder()
                .nome(dto.nome())
                .hex(dto.hex())
                .ativo(dto.ativo() != null ? dto.ativo() : true)
                .build();
    }
}
