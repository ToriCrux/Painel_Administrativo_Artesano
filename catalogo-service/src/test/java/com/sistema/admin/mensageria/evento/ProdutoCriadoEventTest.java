package com.sistema.admin.mensageria.evento;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProdutoCriadoEventTest {

    @Test
    @DisplayName("ProdutoCriadoEvent deve expor dados através dos accessors")
    void accessorsShouldReturnData() {
        ProdutoCriadoEvent event = new ProdutoCriadoEvent(1L, "COD-1", "Mouse", true);

        assertThat(event.produtoId()).isEqualTo(1L);
        assertThat(event.codigo()).isEqualTo("COD-1");
        assertThat(event.nome()).isEqualTo("Mouse");
        assertThat(event.ativo()).isTrue();
    }

    @Test
    @DisplayName("ProdutoCriadoEvent deve implementar equals/hashCode por valor")
    void equalsAndHashCodeShouldBeByValue() {
        ProdutoCriadoEvent e1 = new ProdutoCriadoEvent(1L, "COD-1", "Mouse", true);
        ProdutoCriadoEvent e2 = new ProdutoCriadoEvent(1L, "COD-1", "Mouse", true);

        assertThat(e1).isEqualTo(e2);
        assertThat(e1.hashCode()).isEqualTo(e2.hashCode());
    }
}
