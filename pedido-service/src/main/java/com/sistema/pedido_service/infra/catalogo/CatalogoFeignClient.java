package com.sistema.pedido_service.infra.catalogo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.Map;

@FeignClient(name = "CATALOGO-SERVICE")
public interface CatalogoFeignClient {

    // Busca produto pelo ID — endpoint já existente
    @GetMapping("/api/v1/produtos/{id}")
    Map<String, Object> buscarProdutoPorId(
            @PathVariable("id") Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    );

    // Busca lista de produtos (pagina 0)
    @GetMapping("/api/v1/produtos?page=0&size=100")
    Map<String, Object> listarProdutos(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    );
}
