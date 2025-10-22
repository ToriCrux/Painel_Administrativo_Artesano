package com.sistema.autenticacao_service.api;

import com.sistema.autenticacao_service.api.dto.LoginResponse;
import com.sistema.autenticacao_service.api.dto.RegistroResponse;
import com.sistema.autenticacao_service.api.dto.TokenResponse;
import com.sistema.autenticacao_service.api.dto.UsuarioResponse;
import com.sistema.autenticacao_service.aplicacao.AutenticacaoService;
import com.sistema.autenticacao_service.dominio.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;

    @GetMapping("/users")
    public ResponseEntity<Collection<Usuario>> listar() {
        return ResponseEntity.ok(autenticacaoService.listar());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody @Valid RegistroResponse registroResponse) {
        UsuarioResponse usuario = autenticacaoService.registrar(registroResponse);

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
