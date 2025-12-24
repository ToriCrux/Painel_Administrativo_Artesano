package com.sistema.proposta_service.infra.mensageria;

import com.sistema.proposta_service.config.RabbitConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropostaProducer {

    private final RabbitTemplate rabbitTemplate;

    public void enviar(PropostaCriadaEvent event, String authorization) {
        // 🔹 Caso o token não tenha vindo como parâmetro, tenta extrair automaticamente
        if (!StringUtils.hasText(authorization)) {
            authorization = extrairTokenDaRequisicaoAtual();
        }

        final String tokenFinal = authorization; // ✅ precisa ser final para uso na lambda

        log.info("📨 Publicando PropostaCriadaEvent codigoProposta={} itens={}. Token presente: {}",
                event.codigoProposta(),
                event.produtos() == null ? 0 : event.produtos().size(),
                StringUtils.hasText(tokenFinal) ? "SIM" : "NÃO");

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_PROPOSTAS,
                RabbitConfig.ROUTING_KEY,
                event,
                message -> {
                    // ✅ Propaga o token JWT no header da mensagem RabbitMQ
                    if (StringUtils.hasText(tokenFinal)) {
                        message.getMessageProperties().setHeader(HttpHeaders.AUTHORIZATION, tokenFinal);
                    }
                    return message;
                }
        );
    }

    /**
     * 🔍 Extrai o token JWT do header ou do cookie da requisição atual.
     * Funciona para tokens HttpOnly enviados automaticamente pelo navegador.
     */
    private String extrairTokenDaRequisicaoAtual() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return null;

            HttpServletRequest request = attrs.getRequest();
            if (request == null) return null;

            // 1️⃣ Tenta extrair do header Authorization
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (StringUtils.hasText(header)) {
                return header;
            }

            // 2️⃣ Caso contrário, tenta pegar do cookie (JWT HttpOnly)
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("jwt".equals(cookie.getName()) || "token".equals(cookie.getName())) {
                        return "Bearer " + cookie.getValue();
                    }
                }
            }

            return null;
        } catch (Exception e) {
            log.warn("⚠ Falha ao extrair token da requisição: {}", e.getMessage());
            return null;
        }
    }
}
