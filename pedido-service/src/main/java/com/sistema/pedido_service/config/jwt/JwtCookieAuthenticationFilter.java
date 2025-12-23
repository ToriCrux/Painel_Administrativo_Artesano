package com.sistema.pedido_service.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtCookieAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                // 🔹 Usa o mesmo nome "jwt" que vem do front
                if ("jwt".equals(cookie.getName()) && request.getHeader("Authorization") == null) {
                    String jwt = cookie.getValue();
                    if (jwt != null && !jwt.isBlank()) {
                        request = new HttpServletRequestWrapperWithAuthHeader(request, "Bearer " + jwt);
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
