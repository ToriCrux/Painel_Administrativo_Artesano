package com.sistema.autenticacao_service.api;

import com.sistema.autenticacao_service.api.dto.LoginResponse;
import com.sistema.autenticacao_service.api.dto.RegistroResponse;
import com.sistema.autenticacao_service.api.dto.TokenResponse;
import com.sistema.autenticacao_service.api.dto.UsuarioResponse;
import com.sistema.autenticacao_service.aplicacao.AutenticacaoService;
import com.sistema.autenticacao_service.dominio.Usuario;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

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
    public ResponseEntity<?> login(@RequestBody @Valid LoginResponse loginRequest, HttpServletResponse response) {
        TokenResponse tokenResponse = autenticacaoService.login(loginRequest);
        String jwtToken = tokenResponse.getToken();

        // Cria cookie seguro
        ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken)
                .httpOnly(true)
                .secure(false) // altere para true em produção (HTTPS)
                .sameSite("None") // use "None" se o frontend estiver em domínio diferente
                .path("/")
                .maxAge(3600)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(Map.of("message", "Login realizado com sucesso"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.ok(Map.of("message", "Logout realizado com sucesso"));
    }
}
