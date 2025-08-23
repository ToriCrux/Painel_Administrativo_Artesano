package com.sistema.admin.identidade.controle;

import com.sistema.admin.controle.dto.LoginDTO;
import com.sistema.admin.controle.dto.RegistroDTO;
import com.sistema.admin.controle.dto.TokenDTO;
import com.sistema.admin.controle.dto.UsuarioRespostaDTO;
import com.sistema.admin.identidade.aplicacao.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioRespostaDTO> registrar(@RequestBody @Valid RegistroDTO dto) {
        UsuarioRespostaDTO resp = authService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody @Valid LoginDTO dto) {
        TokenDTO token = authService.login(dto);
        return ResponseEntity.ok(token);
    }
}
