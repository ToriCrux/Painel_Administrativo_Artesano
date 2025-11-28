package com.sistema.estoque_service.mensageria;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;

import static org.assertj.core.api.Assertions.assertThat;

class RabbitMQConfigTest {

    @Test
    @DisplayName("Beans do RabbitMQConfig devem usar nomes/keys definidos nas constantes")
    void beans_ok() {
        RabbitMQConfig cfg = new RabbitMQConfig();

        var exchange = cfg.produtoExchange();
        var queue = cfg.produtoCriadoQueue();
        Binding binding = cfg.produtoCriadoBinding(queue, exchange);

        assertThat(exchange.getName()).isEqualTo(RabbitMQConfig.PRODUTO_EXCHANGE);
        assertThat(queue.getName()).isEqualTo(RabbitMQConfig.PRODUTO_CRIADO_QUEUE);
        assertThat(binding.getRoutingKey()).isEqualTo(RabbitMQConfig.PRODUTO_CRIADO_ROUTING_KEY);

        assertThat(cfg.jackson2JsonMessageConverter()).isNotNull();
    }
}
