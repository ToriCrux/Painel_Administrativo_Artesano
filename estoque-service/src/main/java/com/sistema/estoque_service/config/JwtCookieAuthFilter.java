package com.sistema.estoque_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtCookieAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Se o header já existe, não faz nada
        if (request.getHeader("Authorization") == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                    final String token = cookie.getValue();

                    // Cria um wrapper que adiciona o header Authorization dinamicamente
                    HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
                        @Override
                        public String getHeader(String name) {
                            if ("Authorization".equalsIgnoreCase(name)) {
                                return "Bearer " + token;
                            }
                            return super.getHeader(name);
                        }
                    };

                    filterChain.doFilter(wrappedRequest, response);
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
