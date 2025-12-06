package com.sistema.admin.catalogo.produtoimagem.api;

import com.sistema.admin.catalogo.produtoimagem.aplicacao.ProdutoImagemService;
import com.sistema.admin.catalogo.produtoimagem.dominio.ProdutoImagem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/produtos/{produtoId}/imagens")
@RequiredArgsConstructor
public class ProdutoImagemController {

    private final ProdutoImagemService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Long> upload(@PathVariable Long produtoId,
                                       @RequestParam("file") MultipartFile file,
                                       @RequestParam(value = "principal", required = false) Boolean principal,
                                       @RequestParam(value = "ordem", required = false) Integer ordem) {
        Long id = service.adicionarImagem(produtoId, file, principal, ordem);
        return ResponseEntity.ok(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<ImagemResumo> listar(@PathVariable Long produtoId) {
        return service.listar(produtoId).stream().map(ImagemResumo::from).toList();
    }

    @GetMapping("/{imagemId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<byte[]> download(@PathVariable Long produtoId, @PathVariable Long imagemId) {
        ProdutoImagem img = service.obter(produtoId, imagemId);
        String ct = img.getContentType() != null ? img.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        String nome = img.getNomeArquivo() != null ? img.getNomeArquivo() : ("produto-" + produtoId + "-imagem-" + imagemId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nome + "\"")
                .contentType(MediaType.parseMediaType(ct))
                .body(img.getData());
    }

    @DeleteMapping("/{imagemId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> remover(@PathVariable Long produtoId, @PathVariable Long imagemId) {
        service.remover(produtoId, imagemId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{imagemId}/principal")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> marcarPrincipal(@PathVariable Long produtoId, @PathVariable Long imagemId) {
        service.tornarPrincipal(produtoId, imagemId);
        return ResponseEntity.noContent().build();
    }

    public record ImagemResumo(Long id, String contentType, String nomeArquivo, Long tamanhoBytes, Boolean principal, Integer ordem) {
        public static ImagemResumo from(ProdutoImagem i) {
            return new ImagemResumo(i.getId(), i.getContentType(), i.getNomeArquivo(), i.getTamanhoBytes(), i.getPrincipal(), i.getOrdem());
        }
    }
}