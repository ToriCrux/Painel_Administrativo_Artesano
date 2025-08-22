package com.sistema.admin.identidade.aplicacao;

import com.sistema.admin.controle.dto.LoginDTO;
import com.sistema.admin.controle.dto.RegistroDTO;
import com.sistema.admin.controle.dto.UsuarioRespostaDTO;
import com.sistema.admin.identidade.dominio.Role;
import com.sistema.admin.identidade.dominio.Usuario;
import com.sistema.admin.identidade.infra.RoleRepository;
import com.sistema.admin.identidade.infra.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;

    public AuthService(UsuarioRepository usuarioRepo, RoleRepository roleRepo, PasswordEncoder encoder) {
        this.usuarioRepo = usuarioRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public UsuarioRespostaDTO login(LoginDTO dto) {
        Usuario u = usuarioRepo.findByEmail(dto.email())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (!encoder.matches(dto.senha(), u.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }

        Set<String> roles = u.getRoles()
                .stream()
                .map(r -> r.getNome())
                .collect(Collectors.toSet());

        return new UsuarioRespostaDTO(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getAtivo(),
                roles
        );
    }

    @Transactional
    public UsuarioRespostaDTO registrar(RegistroDTO dto) {
        if (usuarioRepo.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }

        Role roleBasica = roleRepo.findByNome("USUARIO")
                .orElseThrow(() -> new EntityNotFoundException("Role USUARIO não encontrada (seed faltando)"));

        Usuario u = new Usuario();
        u.setNome(dto.nome());
        u.setEmail(dto.email());
        u.setPasswordHash(encoder.encode(dto.senha()));
        u.setAtivo(true);
        u.getRoles().add(roleBasica);

        usuarioRepo.save(u);

        // converte Set<Role> -> Set<String> com os nomes das roles
        Set<String> roles = u.getRoles()
                .stream()
                .map(Role::getNome)
                .collect(Collectors.toSet());

        return new UsuarioRespostaDTO(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getAtivo(),
                roles
        );
    }
}
