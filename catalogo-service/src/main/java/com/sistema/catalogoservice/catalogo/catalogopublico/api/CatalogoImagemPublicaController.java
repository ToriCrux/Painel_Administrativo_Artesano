package com.sistema.catalogoservice.catalogo.catalogopublico.api;

import com.sistema.catalogoservice.catalogo.produtoimagem.aplicacao.ProdutoImagemService;
import com.sistema.catalogoservice.catalogo.produtoimagem.dominio.ProdutoImagem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/public/produtos/{produtoId}/imagens")
@RequiredArgsConstructor
public class CatalogoImagemPublicaController {

    private final ProdutoImagemService produtoImagemService;

    @GetMapping("/{imagemId}")
    public ResponseEntity<byte[]> exibirImagemPublica(
            @PathVariable Long produtoId,
            @PathVariable Long imagemId
    ) {
        ProdutoImagem imagem = produtoImagemService.obter(produtoId, imagemId);

        String contentType = imagem.getContentType() != null
                ? imagem.getContentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        String nomeArquivo = imagem.getNomeArquivo() != null
                ? imagem.getNomeArquivo()
                : ("produto-" + produtoId + "-imagem-" + imagemId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomeArquivo + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(imagem.getData());
    }
}
