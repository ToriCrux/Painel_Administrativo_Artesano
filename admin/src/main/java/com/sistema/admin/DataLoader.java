package com.sistema.admin;

import com.sistema.admin.estoque.aplicacao.EstoqueService;
import com.sistema.admin.estoque.infra.EstoqueRepository;
import com.sistema.admin.auth.aplicacao.AutenticacaoService;
import com.sistema.admin.auth.dominio.Role;
import com.sistema.admin.auth.dominio.Usuario;
import com.sistema.admin.auth.infra.RoleRepository;
import com.sistema.admin.auth.infra.UsuarioRepository;
import com.sistema.admin.catalogo.categoria.aplicacao.CategoriaService;
import com.sistema.admin.catalogo.categoria.infra.CategoriaRepository;
import com.sistema.admin.catalogo.cor.aplicacao.CorService;
import com.sistema.admin.catalogo.cor.infra.CorRepository;
import com.sistema.admin.catalogo.produto.aplicacao.ProdutoService;
import com.sistema.admin.catalogo.produto.infra.ProdutoRepository;
import com.sistema.admin.config.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final AutenticacaoService autenticacaoService;
    private final CategoriaService categoriaService;
    private final CorService corService;
    private final ProdutoService produtoService;
    private final EstoqueService estoqueService;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final CategoriaRepository categoriaRepository;
    private final CorRepository corRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;


    @Override
    public void run(ApplicationArguments args) {

        Set<Role> roles = new HashSet<>();
        Role roleUser = roleRepository.findByNome("ROLE_ADMIN")
                .orElseThrow(() -> new NotFoundException("Role padrão ROLE_ADMIN não encontrada."));
        roles.add(roleUser);

        Usuario administrador = new Usuario();
        administrador.setNome("administrador");
        administrador.setEmail("administrador@email.com");
        administrador.setPasswordHash(passwordEncoder.encode("administrador"));
        administrador.setAtivo(Boolean.TRUE);
        administrador.setRoles(roles);

        try {

            if (usuarioRepository.existsByEmail(administrador.getEmail())) {
                System.out.println("Administrador já cadastrado no sistema.");
            } else {
                usuarioRepository.save(administrador);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("Dados adicionados com sucesso!");
    }
}
