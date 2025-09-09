package com.sistema.admin.catalogo.produto.aplicacao;

import com.sistema.admin.catalogo.categoria.infra.CategoriaRepository;
import com.sistema.admin.catalogo.cor.infra.CorRepository;
import com.sistema.admin.catalogo.produto.dominio.Produto;

import com.sistema.admin.catalogo.produto.infra.ProdutoRepository;
import com.sistema.admin.controle.dto.produto.ProdutoRequestDTO;
import com.sistema.admin.controle.dto.produto.ProdutoResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final CorRepository corRepository;

    public Page<ProdutoResponseDTO> listar(String nome, Pageable pageable) {
        var page = (nome != null && !nome.isBlank())
                ? produtoRepository.findByNomeContainingIgnoreCase(nome, pageable)
                : produtoRepository.findAll(pageable);

        return page.map(this::toResponse);
    }

    public ProdutoResponseDTO salvar(ProdutoRequestDTO dto) {
        produtoRepository.findByCodigoIgnoreCase(dto.codigo())
                .ifPresent(p -> { throw new IllegalArgumentException("Código já existe"); });

        var categoria = categoriaRepository.findByNomeIgnoreCase(dto.categoriaNome())
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada: " + dto.categoriaNome()));


        var cores = dto.corIds().stream()
                .map(id -> corRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Cor não encontrada: " + id)))
                .collect(Collectors.toSet());

        var produto = Produto.builder()
                .codigo(dto.codigo())
                .nome(dto.nome())
                .categoria(categoria)
                .cores(cores)
                .medidas(dto.medidas())
                .precoUnitario(dto.precoUnitario())
                .ativo(dto.ativo())
                .build();

        return toResponse(produtoRepository.save(produto));
    }

    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        var produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        if (!produto.getCodigo().equalsIgnoreCase(dto.codigo())) {
            produtoRepository.findByCodigoIgnoreCase(dto.codigo())
                    .ifPresent(p -> { throw new IllegalArgumentException("Código já existe"); });
        }

        var categoria = categoriaRepository.findByNomeIgnoreCase(dto.categoriaNome())
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada: " + dto.categoriaNome()));


        var cores = dto.corIds().stream()
                .map(corId -> corRepository.findById(corId)
                        .orElseThrow(() -> new EntityNotFoundException("Cor não encontrada: " + corId)))
                .collect(Collectors.toSet());

        produto.setCodigo(dto.codigo());
        produto.setNome(dto.nome());
        produto.setCategoria(categoria);
        produto.setCores(cores);
        produto.setMedidas(dto.medidas());
        produto.setPrecoUnitario(dto.precoUnitario());
        produto.setAtivo(dto.ativo());

        return toResponse(produtoRepository.save(produto));
    }

    public void deletar(Long id) {
        var produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        produto.setAtivo(false); // soft delete
        produtoRepository.save(produto);
    }

    private ProdutoResponseDTO toResponse(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getCodigo(),
                produto.getNome(),
                new com.sistema.admin.controle.dto.categoria.CategoriaResponseDTO(
                        produto.getCategoria().getId(),
                        produto.getCategoria().getNome(),
                        produto.getCategoria().getAtivo(),
                        produto.getCategoria().getCriadoEm(),
                        produto.getCategoria().getAtualizadoEm()
                ),
                produto.getCores().stream()
                        .map(c -> new com.sistema.admin.controle.dto.cor.CorResponseDTO(
                                c.getId(), c.getNome(), c.getHex(),
                                c.getAtivo(), c.getCriadoEm(), c.getAtualizadoEm()
                        ))
                        .collect(Collectors.toSet()),
                produto.getMedidas(),
                produto.getPrecoUnitario(),
                produto.getAtivo(),
                produto.getCriadoEm(),
                produto.getAtualizadoEm()
        );
    }
}
