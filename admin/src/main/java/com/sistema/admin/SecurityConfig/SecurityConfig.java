package com.sistema.admin.SecurityConfig; // se quiser, renomeie depois para com.sistema.admin.config

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // H2 precisa que o CSRF ignore o caminho do console
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/h2-console/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
                ))
                // H2 abre em um <frame>, então precisamos liberar isso
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(auth -> auth
                        // Swagger/OpenAPI + H2 liberados
                        .requestMatchers(
                                "/h2-console/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // demais rotas continuam protegidas
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
