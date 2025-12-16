package com.sistema.proposta_service.infra.mensageria;

import com.sistema.proposta_service.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropostaProducer {

    private final RabbitTemplate rabbitTemplate;

    public void enviar(PropostaCriadaEvent event, String authorization) {
        log.info("📨 Publicando PropostaCriadaEvent codigoProposta={} itens={}. Token presente: {}",
                event.codigoProposta(),
                event.produtos() == null ? 0 : event.produtos().size(),
                StringUtils.hasText(authorization) ? "SIM" : "NÃO"); // Log de confirmação

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_PROPOSTAS,
                RabbitConfig.ROUTING_KEY,
                event,
                message -> {
                    // Se o token existe, ele é propagado para o header AMQP
                    if (StringUtils.hasText(authorization)) {
                        message.getMessageProperties().setHeader(HttpHeaders.AUTHORIZATION, authorization);
                    }
                    return message;
                }
        );
    }
}