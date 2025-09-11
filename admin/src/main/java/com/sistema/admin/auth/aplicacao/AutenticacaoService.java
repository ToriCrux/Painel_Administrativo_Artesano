package com.sistema.admin.auth.aplicacao;

import com.sistema.admin.auth.api.dto.LoginResponse;
import com.sistema.admin.auth.api.dto.RegistroResponse;
import com.sistema.admin.auth.api.dto.TokenResponse;
import com.sistema.admin.auth.api.dto.UsuarioResponse;
import com.sistema.admin.auth.dominio.Role;
import com.sistema.admin.auth.dominio.Usuario;
import com.sistema.admin.auth.infra.RoleRepository;
import com.sistema.admin.auth.infra.UsuarioRepository;
import com.sistema.admin.config.exception.ConflictException;
import com.sistema.admin.config.exception.NotFoundException;
import com.sistema.admin.config.jwt.JwtUtil;
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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public TokenResponse login(LoginResponse loginResponse) {
        final String email = Objects.requireNonNull(loginResponse.email(), "email é obrigatório");
        final String senha = Objects.requireNonNull(loginResponse.senha(), "senha é obrigatória");

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas."));

        if (!passwordEncoder.matches(senha, usuario.getPasswordHash())) {
            throw new BadCredentialsException("Credenciais inválidas.");
        }

        UserDetails principal = toSpringUserDetails(usuario);
        String token = jwtUtil.generateToken(principal);
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

        Set<Role> roles = new HashSet<>();
        Role roleUser = roleRepository.findByNome("ROLE_USER")
                .orElseThrow(() -> new NotFoundException("Role padrão ROLE_USER não encontrada."));
        roles.add(roleUser);

        Usuario novo = new Usuario();
        novo.setNome(nome);
        novo.setEmail(email);
        novo.setPasswordHash(passwordEncoder.encode(senha));
        novo.setAtivo(Boolean.TRUE);
        novo.setRoles(roles);

        Usuario salvo = usuarioRepository.save(novo);

        return new UsuarioResponse(
                salvo.getId(),
                salvo.getNome(),
                salvo.getEmail(),
                salvo.getAtivo(),
                salvo.getRoles() == null ? Set.of()
                        : salvo.getRoles().stream().map(Role::getNome).collect(Collectors.toSet())
        );
    }

    private UserDetails toSpringUserDetails(Usuario usuario) {
        Collection<SimpleGrantedAuthority> authorities =
                usuario.getRoles() == null ? Set.of()
                        : usuario.getRoles().stream()
                        .map(r -> new SimpleGrantedAuthority(r.getNome()))
                        .collect(Collectors.toSet());

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
