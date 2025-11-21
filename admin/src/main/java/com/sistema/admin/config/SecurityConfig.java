package com.sistema.admin.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
@EnableMethodSecurity // habilita @PreAuthorize nos controllers
public class SecurityConfig {

	@Bean
	public JwtDecoder jwtDecoder(@Value("${auth.jwt.secret}") String base64Secret) {
		byte[] keyBytes = Base64.getDecoder().decode(base64Secret);
		SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
		return NimbusJwtDecoder.withSecretKey(secretKey).build();
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter gac = new JwtGrantedAuthoritiesConverter();
		gac.setAuthoritiesClaimName("roles"); // vem do JwtUtil.generateToken()
		gac.setAuthorityPrefix("");           // já vem com "ROLE_..."

		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(gac);
		return converter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(
			HttpSecurity http,
			JwtAuthenticationConverter jwtAuthenticationConverter
	) throws Exception {

		http
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(sm ->
					sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.authorizeHttpRequests(auth -> auth
					.requestMatchers(
							"/v3/api-docs/**",
							"/swagger-ui/**",
							"/swagger-ui.html"
					).permitAll()
					.requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
					.anyRequest().authenticated()
			)
			// Resource Server com JWT
			.oauth2ResourceServer(oauth2 -> oauth2
					.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
			)
			// Resposta 401 “seca” quando não autenticado
			.exceptionHandling(ex -> ex
					.authenticationEntryPoint((req, res, e) ->
							res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
			)
			// Sem formulário de login / httpBasic
			.httpBasic(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.cors(Customizer.withDefaults());

		return http.build();
	}
}
