package com.sistema.admin.auth.api;

import com.sistema.admin.auth.api.dto.LoginResponse;
import com.sistema.admin.auth.api.dto.RegistroResponse;
import com.sistema.admin.auth.api.dto.TokenResponse;
import com.sistema.admin.auth.api.dto.UsuarioResponse;
import com.sistema.admin.auth.aplicacao.AutenticacaoService;
import com.sistema.admin.auth.dominio.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/auth")
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;

    public AutenticacaoController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @GetMapping("/users")
    public ResponseEntity<Collection<Usuario>> listar() {
        return ResponseEntity.ok(autenticacaoService.listar());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody @Valid RegistroResponse registroResposta) {
        UsuarioResponse usuario = autenticacaoService.registrar(registroResposta);

        if (usuario != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create user");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginResponse loginResposta) {
        TokenResponse token = autenticacaoService.login(loginResposta);
        return ResponseEntity.ok(token);
    }

}
