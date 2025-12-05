package com.sistema.autenticacao_service.aplicacao;

import com.sistema.autenticacao_service.api.dto.LoginResponse;
import com.sistema.autenticacao_service.api.dto.RegistroResponse;
import com.sistema.autenticacao_service.api.dto.TokenResponse;
import com.sistema.autenticacao_service.api.dto.UsuarioResponse;
import com.sistema.autenticacao_service.config.exception.ConflictException;
import com.sistema.autenticacao_service.config.jwt.JwtUtil;
import com.sistema.autenticacao_service.dominio.Usuario;
import com.sistema.autenticacao_service.infra.UsuarioRepository;
import com.sistema.autenticacao_service.infra.mongo.UserLogService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserLogService userLogService; // ✅ novo serviço para logs no MongoDB

    @Transactional(readOnly = true)
    public TokenResponse login(LoginResponse loginResponse) {
        final String email = Objects.requireNonNull(loginResponse.email(), "email é obrigatório");
        final String senha = Objects.requireNonNull(loginResponse.senha(), "senha é obrigatória");

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Credenciais inválidas."));

        if (!passwordEncoder.matches(senha, usuario.getPasswordHash())) {
            throw new BadCredentialsException("Credenciais inválidas.");
        }

        UserDetails principal = toSpringUserDetails(usuario);
        String token = jwtUtil.generateToken(principal);

        // ✅ registra log de login no MongoDB
        userLogService.registrarAcao(usuario.getEmail(), "LOGIN");

        return new TokenResponse(token);
    }

    @Transactional
    public UsuarioResponse registrar(RegistroResponse registroResponse) {
        final String nome  = Objects.requireNonNull(registroResponse.nome(),  "nome é obrigatório");
        final String email = Objects.requireNonNull(registroResponse.email(), "email é obrigatório");
        final String senha = Objects.requireNonNull(registroResponse.senha(), "senha é obrigatória");

        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflictException("Já existe um usuário cadastrado com este email.");
        }

        Usuario novo = new Usuario();
        novo.setNome(nome);
        novo.setEmail(email);
        novo.setPasswordHash(passwordEncoder.encode(senha));
        novo.setAtivo(Boolean.TRUE);

        Usuario salvo = usuarioRepository.save(novo);

        // ✅ registra log de cadastro no MongoDB
        userLogService.registrarAcao(salvo.getEmail(), "CADASTRO");

        return new UsuarioResponse(
                salvo.getId(),
                salvo.getNome(),
                salvo.getEmail(),
                salvo.getAtivo(),
                salvo.getRole().name()
        );
    }

    private UserDetails toSpringUserDetails(Usuario usuario) {
        String roleName = (usuario.getRole() == null)
                ? "USUARIO"
                : usuario.getRole().name();

        Set<SimpleGrantedAuthority> authorities =
                Set.of(new SimpleGrantedAuthority("ROLE_" + roleName));

        boolean disabled = usuario.getAtivo() != null && !usuario.getAtivo();

        return User.withUsername(usuario.getEmail())
                .password(usuario.getPasswordHash())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(disabled)
                .build();
    }

    public Collection<Usuario> listar() {
        return usuarioRepository.findAll(Sort.by("nome"));
    }
}
