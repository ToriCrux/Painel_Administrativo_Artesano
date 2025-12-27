package com.sistema.pedido_service.aplicacao;

import com.sistema.pedido_service.api.dto.StatusPedido;
import com.sistema.pedido_service.dominio.ItemPedido;
import com.sistema.pedido_service.dominio.Pedido;
import com.sistema.pedido_service.infra.PedidoRepository;
import com.sistema.pedido_service.infra.catalogo.CatalogoFeignClient;
import com.sistema.pedido_service.infra.mensageria.PropostaCriadaEvent;
import com.sistema.pedido_service.infra.mensageria.evento.PedidoFinalizadoEvent;
import com.sistema.pedido_service.infra.mensageria.evento.PedidoProducer;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository repository;
    private final PedidoProducer pedidoProducer;
    private final CatalogoFeignClient catalogoClient;

    public List<Pedido> listarTodos() {
        return repository.findAll();
    }

    /**
     * Cria um ÚNICO pedido com vários itens (um para cada produto da proposta),
     * consultando o catálogo para buscar o ID real do produto.
     */
    @Transactional
    public void criarPedido(PropostaCriadaEvent evento) {
        if (evento == null || evento.produtos() == null || evento.produtos().isEmpty()) {
            log.warn("Evento de proposta inválido ou sem produtos. Ignorando.");
            return;
        }

        log.info("🛠 Criando pedido ÚNICO para proposta={} com {} produtos.",
                evento.codigoProposta(), evento.produtos().size());

        Pedido pedido = Pedido.builder()
                .codigo(evento.codigoProposta())
                .nomeCliente(evento.nomeCliente())
                .status(StatusPedido.EM_ANALISE)
                .total(BigDecimal.ZERO)
                .build();

        BigDecimal totalGeral = BigDecimal.ZERO;

        Map<String, Object> response;
        try {
            response = catalogoClient.listarProdutos(0, 100);
        } catch (FeignException.Unauthorized e) {
            log.error("❌ Catálogo retornou 401 (Unauthorized). " +
                    "Verifique AUTH_JWT_SECRET_BASE64 ou auth.service.token.");
            throw new IllegalStateException("401 ao consultar Catálogo. Verifique configuração de autenticação.", e);
        } catch (Exception e) {
            log.error("❌ Erro ao consultar Catálogo: {}", e.getMessage());
            throw new IllegalStateException("Falha ao consultar Catálogo Service via FeignClient", e);
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> produtosCatalogo = (List<Map<String, Object>>) response.get("content");

        if (produtosCatalogo == null || produtosCatalogo.isEmpty()) {
            log.warn("⚠ Nenhum produto encontrado no Catálogo. Pedido ignorado.");
            return;
        }

        for (var prod : evento.produtos()) {
            String codigoProduto = prod.codigoProduto();

            // 🔍 Buscar produto no catálogo pelo código
            Map<String, Object> produtoEncontrado = produtosCatalogo.stream()
                    .filter(p -> codigoProduto.equalsIgnoreCase((String) p.get("codigo")))
                    .findFirst()
                    .orElse(null);

            if (produtoEncontrado == null) {
                log.warn("⚠ Produto com código {} não encontrado no Catálogo. Ignorando item.", codigoProduto);
                continue;
            }

            Long produtoId = ((Number) produtoEncontrado.get("id")).longValue();
            BigDecimal totalItem = prod.precoUnitario().multiply(BigDecimal.valueOf(prod.quantidade()));

            ItemPedido item = ItemPedido.builder()
                    .produtoId(produtoId)
                    .nomeProduto(prod.nomeProduto())
                    .quantidade(prod.quantidade())
                    .precoUnitario(prod.precoUnitario())
                    .total(totalItem)
                    .pedido(pedido)
                    .build();

            pedido.getItens().add(item);
            totalGeral = totalGeral.add(totalItem);

            log.info("🧩 Item adicionado: {} (ID={}) x{} - Total R$ {}",
                    item.getNomeProduto(), produtoId, item.getQuantidade(), totalItem);
        }

        pedido.setTotal(totalGeral);
        repository.save(pedido);

        log.info("✅ Pedido criado: código={} total={} com {} itens",
                pedido.getCodigo(), totalGeral, pedido.getItens().size());
    }

    /**
     * Atualiza o status de um pedido, com bloqueio para FINALIZADO, EXTRAVIADO e CANCELADO.
     * Quando FINALIZADO, envia eventos de baixa de estoque para cada item.
     */
    @Transactional
    public void atualizarStatus(Long id, String novoStatus) {
        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        // 🔒 Bloqueia alteração de status em estados finais
        if (pedido.getStatus() == StatusPedido.FINALIZADO ||
                pedido.getStatus() == StatusPedido.EXTRAVIADO ||
                pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new IllegalStateException("Pedido com status " + pedido.getStatus() + " não pode ser alterado.");
        }

        StatusPedido status = StatusPedido.valueOf(novoStatus.toUpperCase());
        pedido.setStatus(status);
        repository.save(pedido);

        // 📦 Quando finalizado, dispara evento de baixa de estoque por item
        if (status == StatusPedido.FINALIZADO) {
            for (ItemPedido item : pedido.getItens()) {
                PedidoFinalizadoEvent evento = new PedidoFinalizadoEvent(
                        pedido.getCodigo(),
                        pedido.getNomeCliente(),
                        item.getProdutoId(),
                        item.getQuantidade(),
                        item.getPrecoUnitario(),
                        item.getTotal()
                );

                pedidoProducer.enviarPedidoFinalizado(evento);

                log.info("📦 Evento PedidoFinalizadoEvent publicado: produtoId={} nomeProduto={} quantidade={} pedido={}",
                        item.getProdutoId(),
                        item.getNomeProduto(),
                        item.getQuantidade(),
                        pedido.getCodigo());
            }
        }
    }
}
