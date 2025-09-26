
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
import com.sistema.admin.config.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

	@Mock
	UsuarioRepository usuarioRepository;
	@Mock
	RoleRepository roleRepository;
	@Mock
	JwtUtil jwtUtil;
	@Mock
	PasswordEncoder passwordEncoder;

	@InjectMocks
	AutenticacaoService service;

	Usuario usuarioBase;

	@BeforeEach
	void setup() {
		usuarioBase = new Usuario();
		usuarioBase.setId(1L);
		usuarioBase.setNome("Fulano");
		usuarioBase.setEmail("fulano@example.com");
		usuarioBase.setAtivo(true);
	}

	@Test
	@DisplayName("registrar: deve lançar ConflictException quando email já existe")
	void registrar_conflict() {
		when(usuarioRepository.existsByEmail("existe@example.com")).thenReturn(true);

		RegistroResponse req = new RegistroResponse("Ex", "existe@example.com", "senha");

		assertThatThrownBy(() -> service.registrar(req))
				.isInstanceOf(ConflictException.class);

		verify(usuarioRepository, never()).save(any());
	}

	@Test
	@DisplayName("registrar: cria usuário com roles e retorna UsuarioResponse")
	void registrar_ok() {
		when(usuarioRepository.existsByEmail("novo@example.com")).thenReturn(false);

		Role roleUser = new Role();
		roleUser.setNome("ROLE_USER");

		when(roleRepository.findByNome(anyString()))
				.thenReturn(Optional.of(roleUser));

		ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
		when(usuarioRepository.save(captor.capture())).thenAnswer(inv -> {
			Usuario u = inv.getArgument(0, Usuario.class);
			u.setId(10L);
			return u;
		});

		RegistroResponse req = new RegistroResponse("Novo", "novo@example.com", "senhaSegura");

		UsuarioResponse resp = service.registrar(req);

		assertThat(resp.id()).isEqualTo(10L);
		assertThat(resp.email()).isEqualTo("novo@example.com");
		assertThat(captor.getValue().getNome()).isEqualTo("Novo");
	}

	@Test
	@DisplayName("login: retorna token do JwtUtil")
	void login_ok() {
		Usuario usuarioBase = new Usuario();
		usuarioBase.setEmail("fulano@example.com");
		usuarioBase.setPasswordHash("ENCODED");
		usuarioBase.setAtivo(true);

		when(usuarioRepository.findByEmail("fulano@example.com"))
				.thenReturn(Optional.of(usuarioBase));

		when(passwordEncoder.matches(eq("senhaSegura"), eq("ENCODED")))
				.thenReturn(true);

		when(jwtUtil.generateToken(any(UserDetails.class)))
				.thenReturn("token-123");

		LoginResponse newLogin = new LoginResponse("fulano@example.com", "senhaSegura");
		TokenResponse token = service.login(newLogin);

		assertThat(token.token()).isEqualTo("token-123");

		verify(usuarioRepository).findByEmail("fulano@example.com");
		verify(passwordEncoder).matches("senhaSegura", "ENCODED");
		verify(jwtUtil).generateToken(any(UserDetails.class));
	}

	@Test
	@DisplayName("listar: retorna lista ordenada")
	void listar_ok() {
		when(usuarioRepository.findAll(any(Sort.class))).thenReturn(List.of(usuarioBase));

		assertThat(service.listar()).hasSize(1);
	}
}
