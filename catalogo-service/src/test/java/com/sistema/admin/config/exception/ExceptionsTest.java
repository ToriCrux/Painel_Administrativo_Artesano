package com.sistema.admin.config.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionsTest {

    @Test
    @DisplayName("ConflictException deve armazenar mensagem")
    void conflictExceptionShouldStoreMessage() {
        ConflictException ex = new ConflictException("Conflito de teste");

        assertThat(ex.getMessage()).isEqualTo("Conflito de teste");
    }

    @Test
    @DisplayName("NotFoundException deve armazenar mensagem")
    void notFoundExceptionShouldStoreMessage() {
        NotFoundException ex = new NotFoundException("Recurso não encontrado");

        assertThat(ex.getMessage()).isEqualTo("Recurso não encontrado");
    }
}
