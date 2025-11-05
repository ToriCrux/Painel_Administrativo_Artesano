package com.sistema.admin.catalogo.produtoimagem.aplicacao;

import com.sistema.admin.catalogo.produto.dominio.Produto;
import com.sistema.admin.catalogo.produto.infra.ProdutoRepository;
import com.sistema.admin.catalogo.produtoimagem.dominio.ProdutoImagem;
import com.sistema.admin.catalogo.produtoimagem.infra.ProdutoImagemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoImagemService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoImagemRepository imagemRepository;

    @Transactional
    public Long adicionarImagem(Long produtoId, MultipartFile arquivo, Boolean principal, Integer ordem) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        try {
            ProdutoImagem img = ProdutoImagem.builder()
                    .produto(produto)
                    .data(arquivo.getBytes())
                    .contentType(arquivo.getContentType())
                    .nomeArquivo(arquivo.getOriginalFilename())
                    .tamanhoBytes(arquivo.getSize())
                    .principal(principal != null && principal)
                    .ordem(ordem)
                    .build();

            if (Boolean.TRUE.equals(img.getPrincipal())) {
                imagemRepository.desmarcarPrincipais(produtoId);
            }
            return imagemRepository.save(img).getId();
        } catch (IOException e) {
            throw new RuntimeException("Falha ao ler arquivo de imagem", e);
        }
    }

    @Transactional(readOnly = true)
    public List<ProdutoImagem> listar(Long produtoId) {
        return imagemRepository.findByProdutoIdOrderByPrincipalDescOrdemAscIdAsc(produtoId);
    }

    @Transactional(readOnly = true)
    public ProdutoImagem obter(Long produtoId, Long imagemId) {
        ProdutoImagem img = imagemRepository.findById(imagemId)
                .orElseThrow(() -> new EntityNotFoundException("Imagem não encontrada"));
        if (!img.getProduto().getId().equals(produtoId)) {
            throw new EntityNotFoundException("Imagem não pertence ao produto");
        }
        return img;
    }

    @Transactional
    public void remover(Long produtoId, Long imagemId) {
        ProdutoImagem img = obter(produtoId, imagemId);
        imagemRepository.delete(img);
    }

    @Transactional
    public void tornarPrincipal(Long produtoId, Long imagemId) {
        imagemRepository.desmarcarPrincipais(produtoId);
        ProdutoImagem img = obter(produtoId, imagemId);
        img.setPrincipal(true);
    }
}