package com.sistema.admin.catalogo.cor.api;

import com.sistema.admin.catalogo.cor.aplicacao.CorService;

import com.sistema.admin.catalogo.cor.api.dto.CorRequest;
import com.sistema.admin.catalogo.cor.api.dto.CorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/cores")
@RequiredArgsConstructor
public class CorController {

    private final CorService corService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<CorResponse>> listarCores(
            @RequestParam(required = false) String nome,
            @ParameterObject Pageable pageable) {

        Page<CorResponse> page = corService.listar(nome, pageable);

        if (page.isEmpty()) return ResponseEntity.noContent().build();

        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CorResponse> listarCorPorId(@PathVariable("id") Long id) {
        CorResponse cor = corService.listarPorId(id);
        if (cor == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(cor);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CorResponse> salvarCor(@RequestBody @Valid CorRequest corRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(corService.salvar(corRequest));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CorResponse> atualizarCor(@PathVariable Long id, @RequestBody @Valid CorRequest corRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(corService.atualizar(id, corRequest));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CorResponse> desativar(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(corService.desativar(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        corService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
