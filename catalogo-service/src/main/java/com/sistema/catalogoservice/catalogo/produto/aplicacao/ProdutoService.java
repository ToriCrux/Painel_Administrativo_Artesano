package com.sistema.catalogoservice.catalogo.produto.aplicacao;

import com.sistema.catalogoservice.catalogo.categoria.api.dto.CategoriaResponse;
import com.sistema.catalogoservice.catalogo.categoria.dominio.Categoria;
import com.sistema.catalogoservice.catalogo.categoria.infra.CategoriaRepository;
import com.sistema.catalogoservice.catalogo.cor.api.dto.CorResponse;
import com.sistema.catalogoservice.catalogo.cor.dominio.Cor;
import com.sistema.catalogoservice.catalogo.cor.infra.CorRepository;
import com.sistema.catalogoservice.catalogo.produto.api.dto.ProdutoRequest;
import com.sistema.catalogoservice.catalogo.produto.api.dto.ProdutoResponse;
import com.sistema.catalogoservice.catalogo.produto.dominio.Produto;
import com.sistema.catalogoservice.catalogo.produto.infra.ProdutoRepository;
import com.sistema.catalogoservice.catalogo.produtoimagem.infra.ProdutoImagemRepository;
import com.sistema.catalogoservice.config.exception.ConflictException;
import com.sistema.catalogoservice.config.exception.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final CorRepository corRepository;
    private final ProdutoImagemRepository produtoImagemRepository;

    // =====================================================
    // LISTAR
    // =====================================================
    public Page<ProdutoResponse> listar(String nome, Pageable pageable) {
        var page = (nome != null && !nome.isBlank())
                ? produtoRepository.findByNomeContainingIgnoreCase(nome, pageable)
                : produtoRepository.findAll(pageable);

        return page.map(this::toResponse);
    }

    // =====================================================
    // LISTAR POR ID
    // =====================================================
    public ProdutoResponse listarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado para este id."));
        return toResponse(produto);
    }

    // =====================================================
    // SALVAR
    // =====================================================
    public ProdutoResponse salvar(ProdutoRequest request) {
        produtoRepository.findByCodigoIgnoreCase(request.codigo())
                .ifPresent(p -> { throw new ConflictException("Código de produto já existe!"); });

        // ✅ Categoria principal (obrigatória)
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada: " + request.categoriaId()));

        // ✅ Subcategoria (opcional)
        Categoria subcategoria = null;
        if (request.subcategoriaId() != null) {
            subcategoria = categoriaRepository.findById(request.subcategoriaId())
                    .orElseThrow(() -> new NotFoundException("Subcategoria não encontrada: " + request.subcategoriaId()));

            // ⚠️ Valida se subcategoria pertence à categoria pai correta
            if (subcategoria.getCategoriaPai() == null ||
                    !subcategoria.getCategoriaPai().getId().equals(categoria.getId())) {
                throw new ConflictException("A subcategoria informada não pertence à categoria selecionada.");
            }
        }

        // ✅ Cores
        Set<Cor> cores = request.corIds() != null && !request.corIds().isEmpty()
                ? request.corIds().stream()
                .map(id -> corRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Cor não encontrada: " + id)))
                .collect(Collectors.toSet())
                : new HashSet<>();

        // ✅ Criação do produto
        Produto produto = Produto.builder()
                .codigo(request.codigo())
                .nome(request.nome())
                .categoria(categoria)
                .subcategoria(subcategoria)
                .cores(cores)
                .medidas(request.medidas())
                .precoUnitario(request.precoUnitario())
                .ativo(request.ativo())
                .descricao(request.descricao())
                .build();

        return toResponse(produtoRepository.save(produto));
    }

    // =====================================================
    // ATUALIZAR
    // =====================================================
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        if (!produto.getCodigo().equalsIgnoreCase(request.codigo())) {
            produtoRepository.findByCodigoIgnoreCase(request.codigo())
                    .ifPresent(p -> { throw new ConflictException("Código já existe"); });
        }

        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));

        Categoria subcategoria = null;
        if (request.subcategoriaId() != null) {
            subcategoria = categoriaRepository.findById(request.subcategoriaId())
                    .orElseThrow(() -> new NotFoundException("Subcategoria não encontrada"));

            if (subcategoria.getCategoriaPai() == null ||
                    !subcategoria.getCategoriaPai().getId().equals(categoria.getId())) {
                throw new ConflictException("A subcategoria informada não pertence à categoria selecionada.");
            }
        }

        Set<Cor> cores = request.corIds() != null && !request.corIds().isEmpty()
                ? request.corIds().stream()
                .map(corId -> corRepository.findById(corId)
                        .orElseThrow(() -> new EntityNotFoundException("Cor não encontrada: " + corId)))
                .collect(Collectors.toSet())
                : new HashSet<>();

        produto.setCodigo(request.codigo());
        produto.setNome(request.nome());
        produto.setCategoria(categoria);
        produto.setSubcategoria(subcategoria);
        produto.setCores(cores);
        produto.setMedidas(request.medidas());
        produto.setPrecoUnitario(request.precoUnitario());
        produto.setAtivo(request.ativo());
        produto.setDescricao(request.descricao());

        return toResponse(produtoRepository.save(produto));
    }

    // =====================================================
    // DESATIVAR
    // =====================================================
    public ProdutoResponse desativar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        produto.setAtivo(false);
        return toResponse(produtoRepository.save(produto));
    }

    // =====================================================
    // DELETAR
    // =====================================================
    public void deletar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        produtoRepository.delete(produto);
    }

    // =====================================================
    // HELPERS
    // =====================================================
    private String buildImagemPrincipalUrl(Long produtoId) {
        return produtoImagemRepository
                .findFirstByProdutoIdAndPrincipalTrue(produtoId)
                .map(img -> "/produtos/" + produtoId + "/imagens/" + img.getId())
                .orElse(null);
    }

    private ProdutoResponse toResponse(Produto produto) {
        var principalUrl = buildImagemPrincipalUrl(produto.getId());

        var categoriaResponse = new CategoriaResponse(
                produto.getCategoria().getId(),
                produto.getCategoria().getNome(),
                produto.getCategoria().getAtivo(),
                produto.getCategoria().getCriadoEm(),
                produto.getCategoria().getAtualizadoEm(),
                null
        );

        var subcategoriaResponse = (produto.getSubcategoria() != null)
                ? new CategoriaResponse(
                produto.getSubcategoria().getId(),
                produto.getSubcategoria().getNome(),
                produto.getSubcategoria().getAtivo(),
                produto.getSubcategoria().getCriadoEm(),
                produto.getSubcategoria().getAtualizadoEm(),
                null
        )
                : null;

        return new ProdutoResponse(
                produto.getId(),
                produto.getCodigo(),
                produto.getNome(),
                categoriaResponse,
                subcategoriaResponse,
                produto.getCores().stream()
                        .map(c -> new CorResponse(
                                c.getId(), c.getNome(), c.getHex(),
                                c.getAtivo(), c.getCriadoEm(), c.getAtualizadoEm()
                        ))
                        .collect(Collectors.toSet()),
                produto.getMedidas(),
                produto.getPrecoUnitario(),
                produto.getAtivo(),
                principalUrl,
                produto.getDescricao(),
                produto.getCriadoEm(),
                produto.getAtualizadoEm()
        );
    }
}
