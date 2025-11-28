package com.sistema.admin.catalogo.produto.api.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProdutoRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private ProdutoRequest validRequest() {
        return new ProdutoRequest(
                "COD-123",
                "Mouse Gamer",
                "Periféricos",
                Set.of(1L, 2L),
                "10x5 cm",
                new BigDecimal("199.90"),
                true,
                "Mouse com RGB"
        );
    }

    @Test
    @DisplayName("ProdutoRequest válido não deve gerar violações")
    void validPayload_shouldPassValidation() {
        ProdutoRequest req = validRequest();

        Set<ConstraintViolation<ProdutoRequest>> violations = validator.validate(req);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("ProdutoRequest com precoUnitario negativo deve falhar")
    void negativePrice_shouldFailValidation() {
        ProdutoRequest req = new ProdutoRequest(
                "COD-123",
                "Mouse Gamer",
                "Periféricos",
                Set.of(1L),
                "10x5 cm",
                new BigDecimal("-1.00"),
                true,
                "Mouse com RGB"
        );

        Set<ConstraintViolation<ProdutoRequest>> violations = validator.validate(req);

        assertThat(violations)
                .isNotEmpty()
                .anySatisfy(v -> assertThat(v.getPropertyPath().toString()).isEqualTo("precoUnitario"));
    }

    @Test
    @DisplayName("ProdutoRequest com nome em branco deve falhar")
    void blankName_shouldFailValidation() {
        ProdutoRequest req = new ProdutoRequest(
                "COD-123",
                " ",
                "Periféricos",
                Set.of(1L),
                "10x5 cm",
                new BigDecimal("10.00"),
                true,
                "Mouse"
        );

        Set<ConstraintViolation<ProdutoRequest>> violations = validator.validate(req);

        assertThat(violations)
                .isNotEmpty()
                .anySatisfy(v -> assertThat(v.getPropertyPath().toString()).isEqualTo("nome"));
    }
}
