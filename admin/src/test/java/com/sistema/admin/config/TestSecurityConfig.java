package com.sistema.admin.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.Jwt;

@TestConfiguration
public class TestSecurityConfig {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth
					.requestMatchers("/api/v1/auth/**").permitAll()
					.anyRequest().authenticated())
			.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt); // Simula um servidor OAuth2
		return http.build();
	}

	@Bean
	public JwtDecoder jwtDecoder() {
		Jwt jwt = Jwt.withTokenValue("fake-token")
				.header("alg", "none")
				.subject("test-user")
				.claim("scope", "read write")
				.build();

		return token -> jwt; // Retorna sempre um token válido
	}
}
