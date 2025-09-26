package com.sistema.admin.auth.api.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RegistroResponseTest {

	private static Validator validator;

	@BeforeAll
	static void setupValidator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	@DisplayName("válido: todos os campos dentro dos limites")
	void valido() {
		var dto = new RegistroResponse(
				"Fulana da Silva",
				"fulana@example.com",
				"senhaSegura"
		);

		Set<ConstraintViolation<RegistroResponse>> violations = validator.validate(dto);
		assertThat(violations).isEmpty();
	}

	@Test
	@DisplayName("nome: NotBlank (nulo, vazio ou espaços) viola")
	void nome_notBlank() {
		for (String invalido : new String[]{null, "", "   "}) {
			var dto = new RegistroResponse(invalido, "a@b.com", "segredo1");
			var v = validator.validate(dto);
			assertSinglePropertyViolation(v, "nome");
		}
	}

	@Test
	@DisplayName("nome: Size(max=120) viola quando excede 120")
	void nome_max120() {
		var dto = new RegistroResponse(str(121), "a@b.com", "segredo1");
		var v = validator.validate(dto);
		assertSinglePropertyViolation(v, "nome");
	}

	@Test
	@DisplayName("email: null -> 1 violação (NotBlank)")
	void email_null_apenasNotBlank() {
		var v = validator.validate(new RegistroResponse("Fulano", null, "segredo1"));
		assertSinglePropertyViolation(v, "email");
	}

	@Test
	@DisplayName("email: > 160 -> viola (Size/Email); basta haver violação em 'email'")
	void email_max160() {
		var tooLongLocal = "a".repeat(150);
		var dto = new RegistroResponse("Fulano", tooLongLocal + "@x.com", "segredo1");
		assertContainsProperty(validator.validate(dto), "email");
	}

	@Test
	@DisplayName("senha: null -> 1 violação (NotBlank)")
	void senha_null_apenasNotBlank() {
		var v = validator.validate(new RegistroResponse("Fulano", "a@b.com", null));
		assertSinglePropertyViolation(v, "senha");
	}

	@Test
	@DisplayName("senha: vazio/blank -> contém violação em 'senha' (pode haver NotBlank + Size)")
	void senha_vazio_ou_blank() {
		for (String invalido : new String[]{"", "   "}) {
			var v = validator.validate(new RegistroResponse("Fulano", "a@b.com", invalido));
			assertContainsProperty(v, "senha");
		}
	}

	@Test
	@DisplayName("senha: < 6 -> viola Size(min=6)")
	void senha_min6() {
		var v = validator.validate(new RegistroResponse("Fulano", "a@b.com", "abc12"));
		assertContainsProperty(v, "senha");
	}

	@Test
	@DisplayName("senha: > 100 -> viola Size(max=100)")
	void senha_max100() {
		var v = validator.validate(new RegistroResponse("Fulano", "a@b.com", "A".repeat(101)));
		assertContainsProperty(v, "senha");
	}

	private static String str(int len) {
		return "A".repeat(len);
	}

	private static <T> void assertSinglePropertyViolation(Set<ConstraintViolation<T>> v, String property) {
		assertThat(v).hasSize(1);
		assertThat(v.iterator().next().getPropertyPath().toString()).isEqualTo(property);
	}

	private static <T> void assertContainsProperty(Set<ConstraintViolation<T>> v, String property) {
		assertThat(v).isNotEmpty();
		assertThat(v.stream().anyMatch(cv -> cv.getPropertyPath().toString().equals(property))).isTrue();
	}
}