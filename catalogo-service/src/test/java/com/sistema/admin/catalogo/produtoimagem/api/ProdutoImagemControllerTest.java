package com.sistema.admin.catalogo.produtoimagem.api;

import com.sistema.admin.catalogo.produtoimagem.aplicacao.ProdutoImagemService;
import com.sistema.admin.catalogo.produtoimagem.dominio.ProdutoImagem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoImagemControllerTest {

    @Mock
    private ProdutoImagemService service;

    @InjectMocks
    private ProdutoImagemController controller;

    @Test
    @DisplayName("upload deve delegar para o service e retornar o ID")
    void upload_shouldDelegateToService() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(service.adicionarImagem(eq(1L), eq(file), eq(true), eq(1))).thenReturn(10L);

        ResponseEntity<Long> response = controller.upload(1L, file, true, 1);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(10L);
        verify(service).adicionarImagem(1L, file, true, 1);
    }

    @Test
    @DisplayName("listar deve transformar entidades em ImagemResumo")
    void listar_shouldMapToResumo() {
        ProdutoImagem img = ProdutoImagem.builder()
                .id(10L)
                .contentType("image/png")
                .nomeArquivo("foto.png")
                .tamanhoBytes(123L)
                .principal(true)
                .ordem(1)
                .data(new byte[]{1, 2, 3})
                .build();
        when(service.listar(1L)).thenReturn(List.of(img));

        List<ProdutoImagemController.ImagemResumo> lista = controller.listar(1L);

        assertThat(lista).hasSize(1);
        ProdutoImagemController.ImagemResumo resumo = lista.get(0);
        assertThat(resumo.id()).isEqualTo(10L);
        assertThat(resumo.contentType()).isEqualTo("image/png");
        assertThat(resumo.nomeArquivo()).isEqualTo("foto.png");
        assertThat(resumo.tamanhoBytes()).isEqualTo(123L);
        assertThat(resumo.principal()).isTrue();
        assertThat(resumo.ordem()).isEqualTo(1);
    }

    @Test
    @DisplayName("download deve retornar bytes e headers corretos")
    void download_shouldReturnBytesAndHeaders() {
        byte[] dados = {1, 2, 3};
        ProdutoImagem img = ProdutoImagem.builder()
                .id(10L)
                .contentType("image/png")
                .nomeArquivo("foto.png")
                .data(dados)
                .build();
        when(service.obter(1L, 10L)).thenReturn(img);

        ResponseEntity<byte[]> response = controller.download(1L, 10L);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                .contains("foto.png");
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.parseMediaType("image/png"));
        assertThat(response.getBody()).isEqualTo(dados);
    }

    @Test
    @DisplayName("remover deve delegar para o service")
    void remover_shouldDelegateToService() {
        ResponseEntity<Void> response = controller.remover(1L, 10L);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        verify(service).remover(1L, 10L);
    }

    @Test
    @DisplayName("marcarPrincipal deve delegar para o service")
    void marcarPrincipal_shouldDelegateToService() {
        ResponseEntity<Void> response = controller.marcarPrincipal(1L, 10L);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        verify(service).tornarPrincipal(1L, 10L);
    }
}
