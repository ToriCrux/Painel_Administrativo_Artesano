package com.sistema.admin.catalogo.categoria.controle;

import com.sistema.admin.catalogo.categoria.aplicacao.CategoriaService;

import com.sistema.admin.controle.dto.categoria.CategoriaRequestDTO;
import com.sistema.admin.controle.dto.categoria.CategoriaResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService service;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Page<CategoriaResponseDTO> listar(@RequestParam(required = false) String nome, Pageable pageable) {
        return service.listar(nome, pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CategoriaResponseDTO salvar(@RequestBody @Valid CategoriaRequestDTO dto) {
        return service.salvar(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoriaResponseDTO atualizar(@PathVariable Long id, @RequestBody @Valid CategoriaRequestDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
