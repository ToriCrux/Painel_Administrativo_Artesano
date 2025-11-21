package com.sistema.estoque_service.mensageria;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

	public static final String PRODUTO_EXCHANGE = "produto.exchange";
	public static final String PRODUTO_CRIADO_ROUTING_KEY = "produto.criado";
	public static final String PRODUTO_CRIADO_QUEUE = "estoque.produto-criado.q";

	@Bean
	public TopicExchange produtoExchange() {
		return new TopicExchange(PRODUTO_EXCHANGE);
	}

	@Bean
	public Queue produtoCriadoQueue() {
		return new Queue(PRODUTO_CRIADO_QUEUE, true);
	}

	@Bean
	public Binding produtoCriadoBinding(Queue produtoCriadoQueue, TopicExchange produtoExchange) {
		return BindingBuilder
				.bind(produtoCriadoQueue)
				.to(produtoExchange)
				.with(PRODUTO_CRIADO_ROUTING_KEY);
	}

	@Bean
	public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
}
