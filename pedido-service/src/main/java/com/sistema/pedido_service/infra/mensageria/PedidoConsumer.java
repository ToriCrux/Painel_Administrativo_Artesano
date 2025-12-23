package com.sistema.pedido_service.infra.mensageria;

import com.sistema.pedido_service.aplicacao.PedidoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoConsumer {

    private final PedidoService pedidoService;

    @RabbitListener(queues = RabbitMQConfig.PROPOSTA_CRIADA_QUEUE)
    public void receberMensagem(PropostaCriadaEvent event,
                                @Header(name = HttpHeaders.AUTHORIZATION, required = false) String token) {

        log.info("▶ Evento de Proposta Recebido: {} com {} itens.",
                event.codigoProposta(), event.produtos() == null ? 0 : event.produtos().size());

        try {
            // Se o token existe, injetamos no SecurityContext para que o Feign Interceptor o encontre
            if (token != null && token.startsWith("Bearer ")) {
                String tokenValue = token.replace("Bearer ", "");
                configurarContextoSeguranca(tokenValue);
                log.info("🔑 Token propagado para o contexto de segurança.");
            } else {
                log.warn("⚠ Nenhum token encontrado no header da mensagem para a proposta {}", event.codigoProposta());
            }

            pedidoService.criarPedido(event);
            log.info("✅ Pedidos criados com sucesso para a Proposta: {}", event.codigoProposta());

        } catch (Exception e) {
            log.error("❌ Erro ao processar evento de proposta {}: {}", event.codigoProposta(), e.getMessage());
            throw new AmqpRejectAndDontRequeueException("Falha ao processar evento de proposta: " + e.getMessage(), e);
        } finally {
            // Importante: Limpa o contexto após o processamento para não vazar para outras mensagens
            SecurityContextHolder.clearContext();
        }
    }

    private void configurarContextoSeguranca(String tokenValue) {
        // Criamos um objeto Jwt minimalista apenas para transporte no contexto
        Jwt jwt = Jwt.withTokenValue(tokenValue)
                .header("alg", "HS256") // ajuste conforme seu padrão se necessário
                .claim("sub", "rabbit-consumer")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();

        JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}