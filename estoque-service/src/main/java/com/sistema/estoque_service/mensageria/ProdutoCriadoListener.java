package com.sistema.estoque_service.mensageria;

import com.sistema.estoque_service.aplicacao.EstoqueService;
import com.sistema.estoque_service.mensageria.evento.ProdutoCriadoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
			// ✅ Agora o estoque é criado com nome e código do produto
			estoqueService.criarEstoqueParaProduto(
					event.produtoId(),
					event.nome(),
					event.codigo()
			);

			log.info("✅ Estoque criado com sucesso para produtoId={}", event.produtoId());

		} catch (Exception e) {
			log.error("❌ Erro ao criar estoque para produtoId={}: {}", event.produtoId(), e.getMessage(), e);
		}
	}
}
