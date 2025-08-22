package com.sistema.admin.SecurityConfig;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Aumente o fator (ex.: 12) se quiser mais custo de hash
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Para API REST em dev, simplifique desabilitando CSRF
                .csrf(csrf -> csrf.disable())

                .headers(h -> h.frameOptions(f -> f.disable()))

                .authorizeHttpRequests(auth -> auth
                        // H2 console
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        // Swagger
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Auth público (cadastro/login)
                        .requestMatchers("/api/auth/**").permitAll()
                        // O resto, autenticado
                        .anyRequest().authenticated()
                )

                // Mantém estas duas opções só para dev
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
