
package com.sistema.admin.auth.api.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import static org.assertj.core.api.Assertions.*;

class LoginResponseTest {

	static Validator validator;

	@BeforeAll
	static void init() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	@Test
	void invalid_email_and_short_password_violate_constraints() {
		var dto = new LoginResponse("not-email", "123");
		var violations = validator.validate(dto);
		assertThat(violations).isNotEmpty();
	}

	@Test
	void valid_values_pass() {
		var dto = new LoginResponse("ok@mail.com", "123456");
		var violations = validator.validate(dto);
		assertThat(violations).isEmpty();
	}
}
