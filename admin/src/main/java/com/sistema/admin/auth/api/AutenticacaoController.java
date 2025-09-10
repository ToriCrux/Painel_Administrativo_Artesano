package com.sistema.admin.auth.api;

import com.sistema.admin.auth.api.dto.LoginResponse;
import com.sistema.admin.auth.api.dto.RegistroResponse;
import com.sistema.admin.auth.api.dto.TokenResponse;
import com.sistema.admin.auth.api.dto.UsuarioResponse;
import com.sistema.admin.auth.aplicacao.AutenticacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;

    public AutenticacaoController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> registrar(@RequestBody @Valid RegistroResponse registroResposta) {
        UsuarioResponse usuario = autenticacaoService.registrar(registroResposta);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginResponse loginResposta) {
        TokenResponse token = autenticacaoService.login(loginResposta);
        return ResponseEntity.ok(token);
    }

}
