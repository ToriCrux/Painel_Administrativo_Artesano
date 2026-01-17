package com.sistema.estoque_service.mensageria;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

	// 🔸 EVENTOS DE PRODUTOS
	public static final String PRODUTO_EXCHANGE = "produto.exchange";
	public static final String PRODUTO_CRIADO_ROUTING_KEY = "produto.criado";
	public static final String PRODUTO_CRIADO_QUEUE = "estoque.produto-criado.q";

	// 🔸 EVENTOS DE PEDIDOS FINALIZADOS
	public static final String PEDIDO_EXCHANGE = "pedido.exchange";
	public static final String PEDIDO_FINALIZADO_ROUTING_KEY = "pedido.finalizado";
	public static final String PEDIDO_FINALIZADO_QUEUE = "estoque.pedido-finalizado.q";

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

	// ✅ Conversor JSON
	@Bean
	public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	// ✅ GARANTE que @RabbitListener vai usar JSON converter
	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
			ConnectionFactory connectionFactory,
			Jackson2JsonMessageConverter converter
	) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setMessageConverter(converter);

		// opcional: melhora resiliência
		factory.setDefaultRequeueRejected(false); // não ficar re-enfileirando sem parar em erro de conversão

		return factory;
	}

	// (Opcional) RabbitTemplate também com converter
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(converter);
		return template;
	}
}
