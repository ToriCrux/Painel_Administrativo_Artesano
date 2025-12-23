package com.sistema.pedido_service.infra.mensageria;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // --- PRODUÇÃO DE EVENTOS (pedido.finalizado) ---
    public static final String PEDIDO_EXCHANGE = "pedido.exchange";
    public static final String PEDIDO_FINALIZADO_ROUTING_KEY = "pedido.finalizado";

    @Bean
    public TopicExchange pedidoExchange() {
        return new TopicExchange(PEDIDO_EXCHANGE);
    }

    // --- CONSUMO DE EVENTOS (proposta.criada) ---
    public static final String PROPOSTA_EXCHANGE = "proposta.exchange";
    public static final String PROPOSTA_CRIADA_ROUTING_KEY = "proposta.criada";
    public static final String PROPOSTA_CRIADA_QUEUE = "proposta.criada.queue";

    @Bean
    public TopicExchange propostaExchange() {
        return new TopicExchange(PROPOSTA_EXCHANGE);
    }

    @Bean
    public Queue propostaCriadaQueue() {
        return new Queue(PROPOSTA_CRIADA_QUEUE, true);
    }

    @Bean
    public Binding bindingPropostaCriada(Queue propostaCriadaQueue, TopicExchange propostaExchange) {
        return BindingBuilder.bind(propostaCriadaQueue)
                .to(propostaExchange)
                .with(PROPOSTA_CRIADA_ROUTING_KEY);
    }

    // --- JSON converter (necessário para headers customizados como Authorization) ---
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // --- Template configurado para o conversor JSON ---
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2JsonMessageConverter());
        return template;
    }

    // --- Listener configurado com o mesmo conversor ---
    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter());
        return factory;
    }
}
