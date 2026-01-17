package com.sistema.catalogoservice.mensageria;

import com.sistema.catalogoservice.catalogo.produto.dominio.Produto;
import com.sistema.catalogoservice.mensageria.evento.ProdutoCriadoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProdutoCriadoPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publicarDepoisDoCommit(Produto produto) {
        ProdutoCriadoEvent event = new ProdutoCriadoEvent(
                produto.getId(),
                produto.getCodigo(),
                produto.getNome(),
                produto.getAtivo()
        );

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    enviar(event);
                }
            });
        } else {
            enviar(event);
        }
    }

    private void enviar(ProdutoCriadoEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PRODUTO_EXCHANGE,
                RabbitMQConfig.PRODUTO_CRIADO_ROUTING_KEY,
                event
        );
        log.info("📤 ProdutoCriadoEvent publicado: produtoId={}, codigo={}, nome={}",
                event.produtoId(), event.codigo(), event.nome());
    }
}
