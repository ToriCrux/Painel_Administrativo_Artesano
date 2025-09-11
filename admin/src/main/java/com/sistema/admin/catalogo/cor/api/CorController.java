package com.sistema.admin.catalogo.cor.api;

import com.sistema.admin.catalogo.cor.aplicacao.CorService;

import com.sistema.admin.catalogo.cor.api.dto.CorRequest;
import com.sistema.admin.catalogo.cor.api.dto.CorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cores")
@RequiredArgsConstructor
public class CorController {

    private final CorService service;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Page<CorResponse> listar(@RequestParam(required = false) String nome, Pageable pageable) {
        return service.listar(nome, pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CorResponse salvar(@RequestBody @Valid CorRequest dto) {
        return service.salvar(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CorResponse atualizar(@PathVariable Long id, @RequestBody @Valid CorRequest dto) {
        return service.atualizar(id, dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void desativar(@PathVariable Long id) {
        service.desativar(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
