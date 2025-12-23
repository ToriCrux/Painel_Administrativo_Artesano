package com.sistema.pedido_service.infra.catalogo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(
        name = "CATALOGO-SERVICE",
        configuration = CatalogoFeignConfig.class
)
public interface CatalogoFeignClient {

    @GetMapping("/api/v1/produtos/{id}")
    Map<String, Object> buscarProdutoPorId(@PathVariable("id") Long id);

    @GetMapping("/api/v1/produtos")
    Map<String, Object> listarProdutos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    );
}