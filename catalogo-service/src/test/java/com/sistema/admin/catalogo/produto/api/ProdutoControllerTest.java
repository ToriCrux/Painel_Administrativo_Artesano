package com.sistema.admin.catalogo.produto.api;

import com.sistema.admin.catalogo.produto.api.dto.ProdutoRequest;
import com.sistema.admin.catalogo.produto.api.dto.ProdutoResponse;
import com.sistema.admin.catalogo.produto.aplicacao.ProdutoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoControllerTest {

    @Mock
    private ProdutoService produtoService;

    @InjectMocks
    private ProdutoController controller;

    private ProdutoResponse sampleResponse() {
        return new ProdutoResponse(
                1L,
                "COD-1",
                "Mouse",
                null,
                null,
                "10x5",
                new BigDecimal("100.00"),
                true,
                null,
                null,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
    }

    @Test
    @DisplayName("listarProdutos deve retornar 204 quando página estiver vazia")
    void listarProdutos_shouldReturnNoContentWhenEmpty() {
        when(produtoService.listar(null, PageRequest.of(0, 10)))
                .thenReturn(Page.empty());

        ResponseEntity<Page<ProdutoResponse>> response =
                controller.listarProdutos(null, PageRequest.of(0, 10));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("listarProdutos deve retornar 200 e página quando houver conteúdo")
    void listarProdutos_shouldReturnPageWhenNotEmpty() {
        Page<ProdutoResponse> page =
                new PageImpl<>(List.of(sampleResponse()), PageRequest.of(0, 10), 1);
        when(produtoService.listar(eq("Mouse"), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<ProdutoResponse>> response =
                controller.listarProdutos("Mouse", PageRequest.of(0, 10));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(page);
    }

    @Test
    @DisplayName("listarProdutoPorId deve retornar 200 quando encontrar o produto")
    void listarProdutoPorId_shouldReturnOkWhenFound() {
        ProdutoResponse resp = sampleResponse();
        when(produtoService.listarPorId(1L)).thenReturn(resp);

        ResponseEntity<ProdutoResponse> response = controller.listarProdutoPorId(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(resp);
    }

    @Test
    @DisplayName("listarProdutoPorId deve retornar 404 quando produto for nulo")
    void listarProdutoPorId_shouldReturnNotFoundWhenNull() {
        when(produtoService.listarPorId(99L)).thenReturn(null);

        ResponseEntity<ProdutoResponse> response = controller.listarProdutoPorId(99L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("salvarProduto deve retornar 201 e delegar para o serviço")
    void salvarProduto_shouldCreate() {
        ProdutoRequest req = new ProdutoRequest(
                "COD-1",
                "Mouse",
                "Periféricos",
                null,
                null,
                new BigDecimal("100.00"),
                true,
                null
        );
        ProdutoResponse resp = sampleResponse();
        when(produtoService.salvar(any(ProdutoRequest.class))).thenReturn(resp);

        ResponseEntity<ProdutoResponse> response = controller.salvarProduto(req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(resp);
        verify(produtoService).salvar(req);
    }

    @Test
    @DisplayName("atualizarProduto deve retornar 201 e delegar para o serviço")
    void atualizarProduto_shouldUpdate() {
        ProdutoRequest req = new ProdutoRequest(
                "COD-1",
                "Mouse",
                "Periféricos",
                null,
                null,
                new BigDecimal("100.00"),
                true,
                null
        );
        ProdutoResponse resp = sampleResponse();
        when(produtoService.atualizar(eq(1L), any(ProdutoRequest.class))).thenReturn(resp);

        ResponseEntity<ProdutoResponse> response = controller.atualizarProduto(1L, req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(resp);
        verify(produtoService).atualizar(1L, req);
    }

    @Test
    @DisplayName("desativarProduto deve retornar 201 e delegar para o serviço")
    void desativarProduto_shouldDeactivate() {
        ProdutoResponse resp = sampleResponse();
        when(produtoService.desativar(1L)).thenReturn(resp);

        ResponseEntity<ProdutoResponse> response = controller.desativarProduto(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(resp);
        verify(produtoService).desativar(1L);
    }

    @Test
    @DisplayName("deletar deve delegar para o serviço")
    void deletar_shouldCallService() {
        controller.deletar(1L);

        verify(produtoService).deletar(1L);
    }
}
