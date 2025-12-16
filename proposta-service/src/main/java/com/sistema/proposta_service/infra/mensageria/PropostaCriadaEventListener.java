package com.sistema.proposta_service.infra.mensageria;

import com.sistema.proposta_service.infra.PropostaRepository;
import com.sistema.proposta_service.infra.mensageria.evento.PropostaCriadaDomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PropostaCriadaEventListener {

    private final PropostaRepository propostaRepository;
    private final PropostaProducer producer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(PropostaCriadaDomainEvent ev) {
        var proposta = propostaRepository.findWithClienteAndProdutosById(ev.propostaId())
                .orElse(null);

        if (proposta == null) {
            log.warn("⚠ Proposta {} não encontrada no AFTER_COMMIT. Evento não enviado.", ev.propostaId());
            return;
        }

        producer.enviar(PropostaCriadaEvent.from(proposta), ev.authorization());
    }
}