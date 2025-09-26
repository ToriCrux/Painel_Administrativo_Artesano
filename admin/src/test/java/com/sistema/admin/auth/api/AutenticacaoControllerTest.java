
package com.sistema.admin.auth.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.admin.auth.api.dto.LoginResponse;
import com.sistema.admin.auth.api.dto.RegistroResponse;
import com.sistema.admin.auth.api.dto.TokenResponse;
import com.sistema.admin.auth.api.dto.UsuarioResponse;
import com.sistema.admin.auth.aplicacao.AutenticacaoService;
import com.sistema.admin.auth.dominio.Usuario;
import com.sistema.admin.auth.infra.RoleRepository;
import com.sistema.admin.auth.infra.UsuarioRepository;
import com.sistema.admin.config.jwt.JwtAuthenticationFilter;
import com.sistema.admin.config.jwt.JwtUtil;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeTry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AutenticacaoController.class)
@AutoConfigureMockMvc(addFilters = false)
class AutenticacaoControllerTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	ObjectMapper om;

	@MockBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	@MockBean
	private UsuarioRepository usuarioRepository;
	@MockBean
	private RoleRepository roleRepository;
	@MockBean
	private PasswordEncoder passwordEncoder;

	@MockBean
	private JwtUtil jwtUtil;

	@MockBean
	private AutenticacaoService autenticacaoService;

	@BeforeTry
	void setup() {
		autenticacaoService = Mockito.mock(AutenticacaoService.class);
		AutenticacaoController controller = new AutenticacaoController(autenticacaoService);
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Provide
	Arbitrary<String> nomes() {
		return Arbitraries.strings()
				.alpha()
				.ofMinLength(2).ofMaxLength(20)
				.map(s -> s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase());
	}

	@Provide
	Arbitrary<String> emails() {
		// gera um email simples; pode sofisticar se quiser
		return Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(12)
				.map(local -> local.toLowerCase() + "@example.com");
	}

	@Provide
	Arbitrary<Usuario> usuarioAtivo() {
		return Combinators.combine(nomes(), emails())
				.as((nome, email) -> {
					Usuario u = new Usuario();
					u.setNome(nome);
					u.setEmail(email);
					u.setAtivo(true);
					return u;
				});
	}

	@Provide
	Arbitrary<List<Usuario>> listaUsuarios() {
		return usuarioAtivo()
				.list()
				.ofSize(5)
				.uniqueElements();
	}

	@Property
	@Label("GET /users -> sempre retorna 200 e a lista serializada tem o mesmo tamanho e os mesmos nomes/emails")
	void listarUsuarios_property(@ForAll("listaUsuarios") List<Usuario> usuarios) throws Exception {

		Mockito.when(autenticacaoService.listar()).thenReturn(usuarios);

		String[] expectedNames  = usuarios.stream().map(Usuario::getNome).toArray(String[]::new);
		String[] expectedEmails = usuarios.stream().map(Usuario::getEmail).toArray(String[]::new);

		mvc.perform(get("/api/v1/auth/users"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(usuarios.size())))
				.andExpect(jsonPath("$[*].nome",  containsInAnyOrder(expectedNames)))
				.andExpect(jsonPath("$[*].email", containsInAnyOrder(expectedEmails)));

		Mockito.verify(autenticacaoService, times(1)).listar();
		Mockito.reset(autenticacaoService); // evita vazamento de stub entre *tries*
	}

	@Test
	@DisplayName("POST /api/v1/auth/register -> 201 com UsuarioResponse")
	void registrar_created() throws Exception {

		RegistroResponse registroResponse = new RegistroResponse("Carlos", "carlos@ex.com", "segredo123");
		UsuarioResponse usuarioResponse = new UsuarioResponse(
				10L, "Carlos", "carlos@ex.com", true, java.util.Set.of("ROLE_USER"));

		Mockito.when(autenticacaoService.registrar(registroResponse))
				.thenReturn(usuarioResponse);



		mvc.perform(post("/api/v1/auth/register")
								.contentType(MediaType.APPLICATION_JSON)
								.content(om.writeValueAsString(registroResponse)))
				.andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(10))
				.andExpect(jsonPath("$.nome").value("Carlos"))
				.andExpect(jsonPath("$.email").value("carlos@ex.com"))
				.andExpect(jsonPath("$.ativo").value(true))
				.andExpect(jsonPath("$.roles", containsInAnyOrder("ROLE_USER")));
	}

	@Test
	@DisplayName("POST /api/v1/auth/register -> 400 quando service retornar null")
	void registrar_badRequest_quandoServiceRetornaNull() throws Exception {

		Mockito.when(autenticacaoService.registrar(any())).thenReturn(null);

		Map<String, Object> registroResponse = Map.of(
				"nome", "Dana",
				"email", "dana@ex.com",
				"senha", "abcdef"
		);

		mvc.perform(post("/api/v1/auth/register")
								.contentType(MediaType.APPLICATION_JSON)
								.content(om.writeValueAsString(registroResponse)))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("Failed to create user")));
	}

	@Test
	@DisplayName("POST /api/v1/auth/register -> 400 por validação (payload inválido)")
	void registrar_badRequest_validacao() throws Exception {

		Map<String, Object> registroResponse = Map.of(
				"nome", "",
				"email", "nao-e-email",
				"senha", "123"
		);

		mvc.perform(post("/api/v1/auth/register")
								.contentType(MediaType.APPLICATION_JSON)
								.content(om.writeValueAsString(registroResponse)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST /api/v1/auth/login -> 200 com TokenResponse")
	void login_ok() throws Exception {
		TokenResponse tokenResp = new TokenResponse("jwt-tokennnn");
		Mockito.when(autenticacaoService.login(any()))
				.thenReturn(tokenResp);

		LoginResponse loginResponse = new LoginResponse("user@ex.com", "senha123");

		mvc.perform(post("/api/v1/auth/login")
								.contentType(MediaType.APPLICATION_JSON)
								.content(om.writeValueAsString(loginResponse)))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.token").value("jwt-tokennnn"));
	}

	@Test
	@DisplayName("POST /api/v1/auth/login -> 400 por validação (email/senha inválidos)")
	void login_badRequest_validacao() throws Exception {
		Map<String, Object> loginResponse = Map.of(
				"email", "invalido",
				"senha", "" // NotBlank -> falha
		);

		mvc.perform(post("/api/v1/auth/login")
								.contentType(MediaType.APPLICATION_JSON)
								.content(om.writeValueAsString(loginResponse)))
				.andExpect(status().isBadRequest());
	}
}
