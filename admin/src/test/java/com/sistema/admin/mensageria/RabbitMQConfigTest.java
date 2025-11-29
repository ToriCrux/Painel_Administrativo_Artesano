package com.sistema.admin.mensageria;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import static org.assertj.core.api.Assertions.assertThat;

class RabbitMQConfigTest {

    private final RabbitMQConfig config = new RabbitMQConfig();

    @Test
    @DisplayName("produtoExchange deve criar TopicExchange com nome correto")
    void produtoExchange_shouldCreateExchange() {
        TopicExchange exchange = config.produtoExchange();

        assertThat(exchange).isNotNull();
        assertThat(exchange.getName()).isEqualTo(RabbitMQConfig.PRODUTO_EXCHANGE);
    }

    @Test
    @DisplayName("produtoCriadoQueue deve criar fila durável com nome correto")
    void produtoCriadoQueue_shouldCreateQueue() {
        Queue queue = config.produtoCriadoQueue();

        assertThat(queue).isNotNull();
        assertThat(queue.getName()).isEqualTo(RabbitMQConfig.PRODUTO_CRIADO_QUEUE);
        assertThat(queue.isDurable()).isTrue();
    }

    @Test
    @DisplayName("produtoCriadoBinding deve vincular fila e exchange com routing key correta")
    void produtoCriadoBinding_shouldBindQueueAndExchange() {
        Queue queue = config.produtoCriadoQueue();
        TopicExchange exchange = config.produtoExchange();

        Binding binding = config.produtoCriadoBinding(queue, exchange);

        assertThat(binding).isNotNull();
        assertThat(binding.getExchange()).isEqualTo(exchange.getName());
        assertThat(binding.getRoutingKey()).isEqualTo(RabbitMQConfig.PRODUTO_CRIADO_ROUTING_KEY);
    }

    @Test
    @DisplayName("jackson2JsonMessageConverter deve retornar instância não nula")
    void jacksonConverter_shouldReturnInstance() {
        Jackson2JsonMessageConverter converter = config.jackson2JsonMessageConverter();

        assertThat(converter).isNotNull();
    }
}
