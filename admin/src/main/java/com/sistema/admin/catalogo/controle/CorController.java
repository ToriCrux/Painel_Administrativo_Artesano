package com.sistema.admin.catalogo.controle;

import com.sistema.admin.catalogo.aplicacao.CorService;
import com.sistema.admin.catalogo.dominio.Cor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cores")
@RequiredArgsConstructor
public class CorController {

    private final CorService service;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Page<Cor> listar(@RequestParam(required = false) String nome, Pageable pageable) {
        return service.listar(nome, pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Cor salvar(@RequestBody @Valid Cor cor) {
        return service.salvar(cor);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Cor atualizar(@PathVariable Long id, @RequestBody @Valid Cor cor) {
        return service.atualizar(id, cor);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
