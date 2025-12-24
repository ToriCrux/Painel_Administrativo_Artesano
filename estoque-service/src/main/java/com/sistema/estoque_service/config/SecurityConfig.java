package com.sistema.estoque_service.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtCookieAuthFilter jwtCookieAuthFilter;

	public SecurityConfig(JwtCookieAuthFilter jwtCookieAuthFilter) {
		this.jwtCookieAuthFilter = jwtCookieAuthFilter;
	}

	// =========================
	// 🔹 JWT Decoder (mesmo secret do auth-service)
	// =========================
	@Bean
	public JwtDecoder jwtDecoder(@Value("${auth.jwt.secret}") String base64Secret) {
		byte[] keyBytes = Base64.getDecoder().decode(base64Secret);
		SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
		return NimbusJwtDecoder.withSecretKey(secretKey).build();
	}

	// =========================
	// 🔹 Converter de roles
	// =========================
	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter gac = new JwtGrantedAuthoritiesConverter();
		gac.setAuthoritiesClaimName("roles");
		gac.setAuthorityPrefix(""); // roles já vêm com ROLE_

		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(gac);
		return converter;
	}

	// =========================
	// 🔹 CORS
	// =========================
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("https://seu-frontend.com", "http://localhost:3000", "http://localhost:3001"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	// =========================
	// 🔹 Configuração principal de segurança
	// =========================
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http,
												   JwtAuthenticationConverter jwtAuthenticationConverter)
			throws Exception {

		http
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
						.requestMatchers("/actuator/health", "/actuator/prometheus").permitAll()
						.anyRequest().authenticated()
				)
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
				.exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) ->
						res.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
				.httpBasic(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable);

		// 🔹 Adiciona o filtro de cookie JWT antes da autenticação padrão
		http.addFilterBefore(jwtCookieAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
