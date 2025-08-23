package com.sistema.admin;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SecureProbeControllerTest {
    @GetMapping("/api/secure/probe")
    public Map<String, Object> probe(Authentication auth) {
        return Map.of(
                "authenticated", auth != null,
                "name", auth != null ? auth.getName() : null,
                "authorities", auth != null ? auth.getAuthorities() : null
        );
    }
}
