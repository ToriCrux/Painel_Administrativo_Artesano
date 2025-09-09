package com.sistema.admin.catalogo.produto.controle;

import com.sistema.admin.catalogo.produto.aplicacao.ProdutoService;

import com.sistema.admin.controle.dto.produto.ProdutoRequestDTO;
import com.sistema.admin.controle.dto.produto.ProdutoResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService service;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Page<ProdutoResponseDTO> listar(@RequestParam(required = false) String nome, Pageable pageable) {
        return service.listar(nome, pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ProdutoResponseDTO salvar(@RequestBody @Valid ProdutoRequestDTO dto) {
        return service.salvar(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ProdutoResponseDTO atualizar(@PathVariable Long id, @RequestBody @Valid ProdutoRequestDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
