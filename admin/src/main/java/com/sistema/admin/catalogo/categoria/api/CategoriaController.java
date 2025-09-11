package com.sistema.admin.catalogo.categoria.api;

import com.sistema.admin.catalogo.categoria.aplicacao.CategoriaService;

import com.sistema.admin.catalogo.categoria.api.dto.CategoriaRequest;
import com.sistema.admin.catalogo.categoria.api.dto.CategoriaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService service;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Page<CategoriaResponse> listar(@RequestParam(required = false) String nome, Pageable pageable) {
        return service.listar(nome, pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CategoriaResponse salvar(@RequestBody @Valid CategoriaRequest dto) {
        return service.salvar(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoriaResponse atualizar(@PathVariable Long id, @RequestBody @Valid CategoriaRequest dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deletar(@PathVariable @Valid Long id) {
        service.desativar(id);
    }
}
