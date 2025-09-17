package com.sistema.admin.catalogo.produto.aplicacao;

import com.sistema.admin.estoque.aplicacao.EstoqueService;
import com.sistema.admin.catalogo.categoria.api.dto.CategoriaResponse;
import com.sistema.admin.catalogo.categoria.infra.CategoriaRepository;
import com.sistema.admin.catalogo.cor.api.dto.CorResponse;
import com.sistema.admin.catalogo.cor.infra.CorRepository;
import com.sistema.admin.catalogo.produto.dominio.Produto;

import com.sistema.admin.catalogo.produto.infra.ProdutoRepository;
import com.sistema.admin.catalogo.produto.api.dto.ProdutoRequest;
import com.sistema.admin.catalogo.produto.api.dto.ProdutoResponse;
import com.sistema.admin.config.exception.ConflictException;
import com.sistema.admin.config.exception.NotFoundException;
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
    private final EstoqueService estoqueService;


    public Page<ProdutoResponse> listar(String nome, Pageable pageable) {
        var page = (nome != null && !nome.isBlank())
                ? produtoRepository.findByNomeContainingIgnoreCase(nome, pageable)
                : produtoRepository.findAll(pageable);

        return page.map(this::toResponse);
    }

    public ProdutoResponse listarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado para este id."));
        return toResponse(produto);
    }

    public ProdutoResponse salvar(ProdutoRequest produtoRequest) {
        produtoRepository.findByCodigoIgnoreCase(produtoRequest.codigo())
                .ifPresent(p -> { throw new ConflictException("Código de produto existente!"); });

        var categoria = categoriaRepository.findByNomeIgnoreCase(produtoRequest.categoriaNome())
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada: " + produtoRequest.categoriaNome()));

        var cores = produtoRequest.corIds().stream()
                .map(id -> corRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Cor não encontrada: " + id)))
                .collect(Collectors.toSet());

        var produto = Produto.builder()
                .codigo(produtoRequest.codigo())
                .nome(produtoRequest.nome())
                .categoria(categoria)
                .cores(cores)
                .medidas(produtoRequest.medidas())
                .precoUnitario(produtoRequest.precoUnitario())
                .ativo(produtoRequest.ativo())
                .build();

        // 1. Salva o produto
        var produtoSalvo = produtoRepository.save(produto);

        // 2. Cria estoque inicial (quantidade = 0) para o produto
        estoqueService.criarEstoqueParaProduto(produtoSalvo.getId());

        // 3. Retorna o response do produto
        return toResponse(produtoSalvo);
    }


    public ProdutoResponse atualizar(Long id, ProdutoRequest produtoRequest) {
        var produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        if (!produto.getCodigo().equalsIgnoreCase(produtoRequest.codigo())) {
            produtoRepository.findByCodigoIgnoreCase(produtoRequest.codigo())
                    .ifPresent(p -> { throw new IllegalArgumentException("Código já existe"); });
        }

        var categoria = categoriaRepository.findByNomeIgnoreCase(produtoRequest.categoriaNome())
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada: " + produtoRequest.categoriaNome()));


        var cores = produtoRequest.corIds().stream()
                .map(corId -> corRepository.findById(corId)
                        .orElseThrow(() -> new EntityNotFoundException("Cor não encontrada: " + corId)))
                .collect(Collectors.toSet());

        produto.setCodigo(produtoRequest.codigo());
        produto.setNome(produtoRequest.nome());
        produto.setCategoria(categoria);
        produto.setCores(cores);
        produto.setMedidas(produtoRequest.medidas());
        produto.setPrecoUnitario(produtoRequest.precoUnitario());
        produto.setAtivo(produtoRequest.ativo());

        return toResponse(produtoRepository.save(produto));
    }

    public ProdutoResponse desativar(Long id) {
        var produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        produto.setAtivo(false); // soft delete
        return toResponse(produtoRepository.save(produto));
    }

    public void deletar (Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        produtoRepository.deleteById(produto.getId());
    }

    // usar @Mapper é mais DDD friendly
    private ProdutoResponse toResponse(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getCodigo(),
                produto.getNome(),
                new CategoriaResponse(
                        produto.getCategoria().getId(),
                        produto.getCategoria().getNome(),
                        produto.getCategoria().getAtivo(),
                        produto.getCategoria().getCriadoEm(),
                        produto.getCategoria().getAtualizadoEm()
                ),
                produto.getCores().stream()
                        .map(c -> new CorResponse(
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
