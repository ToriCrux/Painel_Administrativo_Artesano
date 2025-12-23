package com.sistema.proposta_service.config.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class HttpServletRequestWrapperWithAuthHeader extends HttpServletRequestWrapper {

    private final String authHeaderValue;

    public HttpServletRequestWrapperWithAuthHeader(HttpServletRequest request, String authHeaderValue) {
        super(request);
        this.authHeaderValue = authHeaderValue;
    }

    @Override
    public String getHeader(String name) {
        if ("Authorization".equalsIgnoreCase(name)) {
            return authHeaderValue;
        }
        return super.getHeader(name);
    }
}
