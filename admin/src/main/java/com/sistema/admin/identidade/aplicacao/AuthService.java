package com.sistema.admin.identidade.aplicacao;

import com.sistema.admin.controle.dto.LoginDTO;
import com.sistema.admin.controle.dto.RegistroDTO;
import com.sistema.admin.controle.dto.TokenDTO;
import com.sistema.admin.controle.dto.UsuarioRespostaDTO;
import com.sistema.admin.identidade.dominio.Role;
import com.sistema.admin.identidade.dominio.Usuario;
import com.sistema.admin.identidade.infra.RoleRepository;
import com.sistema.admin.identidade.infra.UsuarioRepository;
import com.sistema.admin.identidade.seguranca.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
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
public class AuthService {

    private final UsuarioRepository usuarioRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioRepository usuarioRepo,
                       RoleRepository roleRepo,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.usuarioRepo = usuarioRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional(readOnly = true)
    public TokenDTO login(LoginDTO credenciais) {
        final String email = Objects.requireNonNull(credenciais.email(), "email é obrigatório");
        final String senha = Objects.requireNonNull(credenciais.senha(), "senha é obrigatória");

        Usuario usuario = usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado para o email informado."));

        if (!passwordEncoder.matches(senha, usuario.getPasswordHash())) {
            throw new BadCredentialsException("Credenciais inválidas.");
        }

        UserDetails principal = toSpringUserDetails(usuario);
        String token = jwtUtil.generateToken(principal);
        return new TokenDTO(token);
    }

    @Transactional
    public UsuarioRespostaDTO registrar(RegistroDTO dto) {
        final String nome  = Objects.requireNonNull(dto.nome(),  "nome é obrigatório");
        final String email = Objects.requireNonNull(dto.email(), "email é obrigatório");
        final String senha = Objects.requireNonNull(dto.senha(), "senha é obrigatória");

        if (usuarioRepo.existsByEmail(email)) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com este email.");
        }

        Set<Role> roles = new HashSet<>();
        Role roleUser = roleRepo.findByNome("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Role padrão ROLE_USER não encontrada."));
        roles.add(roleUser);

        Usuario novo = new Usuario();
        novo.setNome(nome);
        novo.setEmail(email);
        novo.setPasswordHash(passwordEncoder.encode(senha));
        novo.setAtivo(Boolean.TRUE);
        novo.setRoles(roles);

        Usuario salvo = usuarioRepo.save(novo);

        return new UsuarioRespostaDTO(
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
}
