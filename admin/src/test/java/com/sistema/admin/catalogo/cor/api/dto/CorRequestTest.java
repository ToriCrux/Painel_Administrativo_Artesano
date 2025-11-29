package com.sistema.admin.catalogo.cor.api.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CorRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("CorRequest válido não deve gerar violações")
    void validPayload_shouldPassValidation() {
        CorRequest req = new CorRequest("Azul Marinho", "#112233", true);

        Set<ConstraintViolation<CorRequest>> violations = validator.validate(req);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("CorRequest com hex inválido deve gerar violação")
    void invalidHex_shouldFailValidation() {
        CorRequest req = new CorRequest("Azul Marinho", "112233", true);

        Set<ConstraintViolation<CorRequest>> violations = validator.validate(req);

        assertThat(violations)
                .isNotEmpty()
                .anySatisfy(v -> assertThat(v.getPropertyPath().toString()).isEqualTo("hex"));
    }
}
