package com.sistema.pedido_service.infra.mensageria;

import com.sistema.pedido_service.aplicacao.PedidoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoConsumer {

    private final PedidoService pedidoService;

    // Use o nome da fila que você definiu no RabbitMQConfig
    @RabbitListener(queues = RabbitMQConfig.PROPOSTA_CRIADA_QUEUE)
    public void receberMensagem(
            PropostaCriadaEvent event,
            @Header(name = HttpHeaders.AUTHORIZATION, required = false) String authorization
    ) {
        log.info("▶ Evento de Proposta Recebido: {} com {} itens.",
                event.codigoProposta(), event.produtos() == null ? 0 : event.produtos().size());

        try {
            // authorization será null se não vier no AMQP e a validação será feita no Service
            pedidoService.criarPedido(event, authorization);
            log.info("✅ Pedidos criados com sucesso para a Proposta: {}", event.codigoProposta());

        } catch (Exception e) {
            log.error("❌ Erro ao processar evento de proposta {}: {}", event.codigoProposta(), e.getMessage());
            // Lança exceção para que o RabbitMQ rejeite a mensagem e não tente o re-enqueue infinito.
            throw new AmqpRejectAndDontRequeueException("Falha ao processar evento de proposta: " + e.getMessage(), e);
        }
    }
}