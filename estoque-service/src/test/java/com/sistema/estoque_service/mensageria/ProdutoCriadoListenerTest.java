package com.sistema.estoque_service.mensageria;

import com.sistema.estoque_service.aplicacao.EstoqueService;
import com.sistema.estoque_service.mensageria.evento.ProdutoCriadoEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProdutoCriadoListenerTest {

    @Mock
    EstoqueService estoqueService;

    @InjectMocks
    ProdutoCriadoListener listener;

    @Test
    @DisplayName("onProdutoCriado: deve criar estoque para o produto recebido")
    void onProdutoCriado_ok() {
        ProdutoCriadoEvent event = new ProdutoCriadoEvent(10L, "COD-10", "Produto X", true);

        listener.onProdutoCriado(event);

        verify(estoqueService).criarEstoqueParaProduto(10L);
    }
}
