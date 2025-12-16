package com.sistema.proposta_service.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_PROPOSTAS = "proposta.exchange"; // <- sem "s"
    public static final String ROUTING_KEY = "proposta.criada";          // <- igual ao binding

    @Bean
    public TopicExchange propostasExchange() {
        return new TopicExchange(EXCHANGE_PROPOSTAS);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

