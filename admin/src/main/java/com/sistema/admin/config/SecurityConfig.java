package com.sistema.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Value("${auth.jwt.secret}")
	private String jwtSecretBase64;

	@Bean
	SecurityFilterChain security(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
					.requestMatchers("/actuator/health", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
					.anyRequest().authenticated()
			)
			.oauth2ResourceServer(oauth -> oauth
					.jwt(jwt -> jwt
							.decoder(jwtDecoder())
							.jwtAuthenticationConverter(jwtAuthConverter())
					)
			);
		return http.build();
	}

	@Bean
	JwtDecoder jwtDecoder() {
		byte[] key = java.util.Base64.getDecoder().decode(jwtSecretBase64);
		javax.crypto.SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(key, "HmacSHA256");
		return NimbusJwtDecoder.withSecretKey(secretKey).build();
	}

	@Bean
	org.springframework.core.convert.converter.Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthConverter() {
		var gac = new JwtGrantedAuthoritiesConverter();
		gac.setAuthoritiesClaimName("roles");
		gac.setAuthorityPrefix("");

		return jwt -> {
			var authorities = gac.convert(jwt);
			String principal = jwt.getClaimAsString("sub");
			return new JwtAuthenticationToken(jwt, authorities, principal);
		};
	}
}

