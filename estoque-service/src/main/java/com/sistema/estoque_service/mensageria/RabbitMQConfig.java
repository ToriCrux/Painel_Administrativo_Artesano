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

	// 🔸 EXISTENTE — eventos de produtos
	public static final String PRODUTO_EXCHANGE = "produto.exchange";
	public static final String PRODUTO_CRIADO_ROUTING_KEY = "produto.criado";
	public static final String PRODUTO_CRIADO_QUEUE = "estoque.produto-criado.q";

	// 🔸 NOVO — eventos de pedidos finalizados
	public static final String PEDIDO_EXCHANGE = "pedido.exchange";
	public static final String PEDIDO_FINALIZADO_ROUTING_KEY = "pedido.finalizado";
	public static final String PEDIDO_FINALIZADO_QUEUE = "estoque.pedido-finalizado.q";

	// ========== CONFIGURAÇÕES EXISTENTES ==========

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

	// ========== NOVAS CONFIGURAÇÕES PARA PEDIDOS ==========

	@Bean
	public TopicExchange pedidoExchange() {
		return new TopicExchange(PEDIDO_EXCHANGE);
	}

	@Bean
	public Queue pedidoFinalizadoQueue() {
		return new Queue(PEDIDO_FINALIZADO_QUEUE, true);
	}

	@Bean
	public Binding pedidoFinalizadoBinding(Queue pedidoFinalizadoQueue, TopicExchange pedidoExchange) {
		return BindingBuilder
				.bind(pedidoFinalizadoQueue)
				.to(pedidoExchange)
				.with(PEDIDO_FINALIZADO_ROUTING_KEY);
	}

	// Conversor JSON (usado para ambos os eventos)
	@Bean
	public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
}
