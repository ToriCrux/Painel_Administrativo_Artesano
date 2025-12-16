package com.sistema.pedido_service.infra.mensageria;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // --- Configuração para ENVIO (Pedido Service é o produtor) ---
    public static final String PEDIDO_EXCHANGE = "pedido.exchange";
    public static final String PEDIDO_FINALIZADO_ROUTING_KEY = "pedido.finalizado";

    @Bean
    public TopicExchange pedidoExchange() {
        return new TopicExchange(PEDIDO_EXCHANGE);
    }
    // ------------------------------------------------------------------

    // --- Configuração para RECEBIMENTO (Pedido Service é o consumidor) ---
    // IMPORTANTE: Essas chaves e nome da Exchange devem ser IDÊNTICOS aos do Proposta Service!
    public static final String PROPOSTA_EXCHANGE = "proposta.exchange";
    public static final String PROPOSTA_CRIADA_ROUTING_KEY = "proposta.criada";
    public static final String PROPOSTA_CRIADA_QUEUE = "proposta.criada.queue"; // O nome da fila deve ser o mesmo do Listener

    @Bean
    public TopicExchange propostaExchange() {
        return new TopicExchange(PROPOSTA_EXCHANGE);
    }

    @Bean
    public Queue propostaCriadaQueue() {
        // durable = true
        return new Queue(PROPOSTA_CRIADA_QUEUE, true);
    }

    @Bean
    public Binding bindingPropostaCriada(Queue propostaCriadaQueue, TopicExchange propostaExchange) {
        // Liga a fila à Exchange usando a chave de roteamento
        return BindingBuilder.bind(propostaCriadaQueue)
                .to(propostaExchange)
                .with(PROPOSTA_CRIADA_ROUTING_KEY);
    }

    // Bean para converter mensagens JSON (necessário para ambos, envio e recebimento)
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}