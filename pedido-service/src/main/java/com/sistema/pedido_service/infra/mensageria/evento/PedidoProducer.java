package com.sistema.pedido_service.infra.mensageria.evento;

import com.sistema.pedido_service.infra.mensageria.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoProducer {

    private final RabbitTemplate rabbitTemplate;

    public void enviarPedidoFinalizado(PedidoFinalizadoEvent event) {
        log.info("🔔 Enviando evento PedidoFinalizadoEvent para produtoId={} quantidade={}",
                event.produtoId(), event.quantidade());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PEDIDO_EXCHANGE,
                RabbitMQConfig.PEDIDO_FINALIZADO_ROUTING_KEY,
                event
        );
    }
}
