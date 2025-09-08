package com.sistema.admin.catalogo.cor.controle;

import com.sistema.admin.catalogo.cor.aplicacao.CorService;

import com.sistema.admin.controle.dto.cor.CorRequestDTO;
import com.sistema.admin.controle.dto.cor.CorResponseDTO;
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
    public Page<CorResponseDTO> listar(@RequestParam(required = false) String nome, Pageable pageable) {
        return service.listar(nome, pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CorResponseDTO salvar(@RequestBody @Valid CorRequestDTO dto) {
        return service.salvar(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CorResponseDTO atualizar(@PathVariable Long id, @RequestBody @Valid CorRequestDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
