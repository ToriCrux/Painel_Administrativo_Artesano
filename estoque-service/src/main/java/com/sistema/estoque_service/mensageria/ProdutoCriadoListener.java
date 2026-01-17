package com.sistema.estoque_service.mensageria;

import com.sistema.estoque_service.aplicacao.EstoqueService;
import com.sistema.estoque_service.mensageria.evento.ProdutoCriadoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProdutoCriadoListener {

	private final EstoqueService estoqueService;

	@RabbitListener(queues = RabbitMQConfig.PRODUTO_CRIADO_QUEUE)
	public void onProdutoCriado(ProdutoCriadoEvent event) {
		log.info("📦 Recebido ProdutoCriadoEvent → produtoId={}, código={}, nome={}",
				event.produtoId(), event.codigo(), event.nome());

		try {
			estoqueService.criarEstoqueParaProdutoSeNaoExistir(
					event.produtoId(),
					event.nome(),
					event.codigo()
			);

			log.info("✅ Estoque OK para produtoId={} (criado ou já existia)", event.produtoId());

		} catch (DataIntegrityViolationException e) {
			// fallback caso exista corrida/duplicidade
			log.warn("⚠️ Estoque já existe para produtoId={} (constraint). Ignorando.", event.produtoId());

		} catch (Exception e) {
			log.error("❌ Erro ao processar ProdutoCriadoEvent produtoId={}: {}",
					event.produtoId(), e.getMessage(), e);
			throw e; // opcional: deixar falhar p/ retry/DLQ conforme sua config
		}
	}
}
