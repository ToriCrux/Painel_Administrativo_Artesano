package com.sistema.estoque_service.mensageria;

import com.sistema.estoque_service.aplicacao.EstoqueService;
import com.sistema.estoque_service.mensageria.evento.PedidoFinalizadoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoFinalizadoListener {

    private final EstoqueService estoqueService;

    @RabbitListener(queues = RabbitMQConfig.PEDIDO_FINALIZADO_QUEUE)
    public void onPedidoFinalizado(PedidoFinalizadoEvent event) {
        log.info("📦 Recebido PedidoFinalizadoEvent: codigo={} produtoId={} qtd={}",
                event.codigo(), event.produtoId(), event.quantidade());

        try {
            estoqueService.registrarSaidaPorPedido(
                    event.produtoId(),
                    event.quantidade().longValue(),
                    event.codigo(),
                    event.nomeCliente()
            );

            log.info("✅ Estoque atualizado com sucesso para produtoId={} (Pedido: {})",
                    event.produtoId(), event.codigo());
        } catch (Exception e) {
            log.error("❌ Erro ao baixar estoque para produtoId={}: {}", event.produtoId(), e.getMessage());
        }
    }
}
