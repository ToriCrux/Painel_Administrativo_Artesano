package com.sistema.admin.catalogo.produtoimagem.aplicacao;

import com.sistema.admin.catalogo.produto.dominio.Produto;
import com.sistema.admin.catalogo.produto.infra.ProdutoRepository;
import com.sistema.admin.catalogo.produtoimagem.dominio.ProdutoImagem;
import com.sistema.admin.catalogo.produtoimagem.infra.ProdutoImagemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoImagemServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ProdutoImagemRepository imagemRepository;

    @InjectMocks
    private ProdutoImagemService service;

    @Test
    @DisplayName("adicionarImagem deve salvar entidade e retornar o ID")
    void adicionarImagem_shouldSaveAndReturnId() throws Exception {
        Long produtoId = 1L;
        Produto produto = Produto.builder().id(produtoId).build();

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto));

        MultipartFile arquivo = mock(MultipartFile.class);
        when(arquivo.getBytes()).thenReturn("dados".getBytes());
        when(arquivo.getContentType()).thenReturn("image/png");
        when(arquivo.getOriginalFilename()).thenReturn("foto.png");
        when(arquivo.getSize()).thenReturn(123L);

        ProdutoImagem salvo = ProdutoImagem.builder()
                .id(10L)
                .produto(produto)
                .contentType("image/png")
                .nomeArquivo("foto.png")
                .tamanhoBytes(123L)
                .principal(true)
                .ordem(1)
                .build();
        when(imagemRepository.save(any(ProdutoImagem.class))).thenReturn(salvo);

        Long idGerado = service.adicionarImagem(produtoId, arquivo, true, 1);

        assertThat(idGerado).isEqualTo(10L);
        verify(imagemRepository).desmarcarPrincipais(produtoId);
        verify(imagemRepository).save(any(ProdutoImagem.class));
    }

    @Test
    @DisplayName("adicionarImagem deve lançar EntityNotFoundException se produto não existir")
    void adicionarImagem_shouldThrowWhenProdutoNotFound() throws Exception {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        MultipartFile arquivo = mock(MultipartFile.class);


        assertThatThrownBy(() -> service.adicionarImagem(99L, arquivo, false, null))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Produto não encontrado");
    }

    @Test
    @DisplayName("listar deve delegar para imagemRepository")
    void listar_shouldDelegateToRepository() {
        List<ProdutoImagem> imagens = List.of(ProdutoImagem.builder().id(1L).build());
        when(imagemRepository.findByProdutoIdOrderByPrincipalDescOrdemAscIdAsc(1L))
                .thenReturn(imagens);

        List<ProdutoImagem> result = service.listar(1L);

        assertThat(result).hasSize(1);
        verify(imagemRepository).findByProdutoIdOrderByPrincipalDescOrdemAscIdAsc(1L);
    }

    @Test
    @DisplayName("obter deve retornar imagem quando pertence ao produto")
    void obter_shouldReturnImageWhenProdutoMatches() {
        Produto produto = Produto.builder().id(1L).build();
        ProdutoImagem imagem = ProdutoImagem.builder()
                .id(5L)
                .produto(produto)
                .build();
        when(imagemRepository.findById(5L)).thenReturn(Optional.of(imagem));

        ProdutoImagem result = service.obter(1L, 5L);

        assertThat(result).isSameAs(imagem);
    }

    @Test
    @DisplayName("obter deve lançar EntityNotFoundException quando imagem não pertence ao produto")
    void obter_shouldThrowWhenImageDoesNotBelongToProduto() {
        Produto produto = Produto.builder().id(2L).build();
        ProdutoImagem imagem = ProdutoImagem.builder()
                .id(5L)
                .produto(produto)
                .build();
        when(imagemRepository.findById(5L)).thenReturn(Optional.of(imagem));

        assertThatThrownBy(() -> service.obter(1L, 5L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Imagem não pertence ao produto");
    }

    @Test
    @DisplayName("remover deve excluir a imagem retornada por obter")
    void remover_shouldDeleteImage() {
        Produto produto = Produto.builder().id(1L).build();
        ProdutoImagem imagem = ProdutoImagem.builder()
                .id(5L)
                .produto(produto)
                .build();
        when(imagemRepository.findById(5L)).thenReturn(Optional.of(imagem));

        service.remover(1L, 5L);

        verify(imagemRepository).delete(imagem);
    }

    @Test
    @DisplayName("tornarPrincipal deve desmarcar demais e marcar imagem como principal")
    void tornarPrincipal_shouldMarkAsPrincipal() {
        Produto produto = Produto.builder().id(1L).build();
        ProdutoImagem imagem = ProdutoImagem.builder()
                .id(5L)
                .produto(produto)
                .principal(false)
                .build();
        when(imagemRepository.findById(5L)).thenReturn(Optional.of(imagem));

        service.tornarPrincipal(1L, 5L);

        verify(imagemRepository).desmarcarPrincipais(1L);
        assertThat(imagem.getPrincipal()).isTrue();
    }
}
