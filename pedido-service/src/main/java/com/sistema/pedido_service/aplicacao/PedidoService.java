package com.sistema.pedido_service.aplicacao;

import com.sistema.pedido_service.api.dto.StatusPedido;
import com.sistema.pedido_service.dominio.Pedido;
import com.sistema.pedido_service.infra.PedidoRepository;
import com.sistema.pedido_service.infra.catalogo.CatalogoFeignClient;
import com.sistema.pedido_service.infra.mensageria.PropostaCriadaEvent;
import com.sistema.pedido_service.infra.mensageria.evento.PedidoFinalizadoEvent;
import com.sistema.pedido_service.infra.mensageria.evento.PedidoProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
     * Cria pedidos automaticamente com base em um evento de proposta criada.
     */
    @Transactional
    public void criarPedido(PropostaCriadaEvent evento, String authorization) {
        if (evento == null) return;
        if (evento.produtos() == null || evento.produtos().isEmpty()) {
            log.warn("Evento de proposta {} não possui produtos. Ignorando.", evento.codigoProposta());
            return;
        }

        if (authorization == null || authorization.isBlank()) {
            throw new IllegalStateException("Authorization Bearer Token ausente no evento RabbitMQ. Não é possível consultar o Catálogo.");
        }

        log.info("🛠 Criando pedidos para proposta={} com {} produtos.",
                evento.codigoProposta(), evento.produtos().size());

        // 🔁 Obtém a lista de produtos do Catálogo (página 0)
        Map<String, Object> response;
        try {
            response = catalogoClient.listarProdutos(authorization);
        } catch (Exception e) {
            log.error("❌ Erro ao consultar o Catálogo Service: {}", e.getMessage());
            throw new IllegalStateException("Falha ao consultar Catálogo Service via FeignClient", e);
        }

        // Extrai o array "content" da resposta (padrão de paginação Spring)
        List<Map<String, Object>> produtosCatalogo = (List<Map<String, Object>>) response.get("content");
        if (produtosCatalogo == null || produtosCatalogo.isEmpty()) {
            log.warn("Nenhum produto retornado do Catálogo. Verifique se há produtos cadastrados.");
            return;
        }

        for (var prod : evento.produtos()) {
            String codigoProduto = prod.codigoProduto();

            // 🔍 Busca o produto no catálogo com base no código
            Optional<Map<String, Object>> produtoEncontradoOpt = produtosCatalogo.stream()
                    .filter(p -> codigoProduto.equalsIgnoreCase((String) p.get("codigo")))
                    .findFirst();

            if (produtoEncontradoOpt.isEmpty()) {
                log.warn("Produto com código {} não encontrado no Catálogo. Ignorando item.", codigoProduto);
                continue;
            }

            Map<String, Object> produtoEncontrado = produtoEncontradoOpt.get();
            Long produtoId = ((Number) produtoEncontrado.get("id")).longValue();

            // Evita duplicidade de pedidos
            if (repository.existsByCodigoAndProdutoId(evento.codigoProposta(), produtoId)) {
                log.info("Pedido já existe para proposta={} produtoId={}. Ignorando duplicado.",
                        evento.codigoProposta(), produtoId);
                continue;
            }

            // Calcula total
            BigDecimal total = prod.precoUnitario().multiply(BigDecimal.valueOf(prod.quantidade()));

            // Cria e persiste o pedido
            Pedido pedido = Pedido.builder()
                    .codigo(evento.codigoProposta())
                    .produtoId(produtoId)
                    .nomeCliente(evento.nomeCliente())
                    .produto(prod.nomeProduto())
                    .quantidade(prod.quantidade())
                    .precoUnitario(prod.precoUnitario())
                    .total(total)
                    .status(StatusPedido.EM_ANALISE)
                    .build();

            repository.save(pedido);

            log.info("✅ Pedido criado: proposta={} produto={} qtd={} total={}",
                    evento.codigoProposta(), prod.nomeProduto(), prod.quantidade(), total);
        }

        log.info("🏁 Processamento da proposta {} concluído.", evento.codigoProposta());
    }

    /**
     * Atualiza o status de um pedido e publica evento se for FINALIZADO.
     */
    @Transactional
    public void atualizarStatus(Long id, String novoStatus) {
        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        if (pedido.getStatus() == StatusPedido.FINALIZADO) {
            throw new IllegalStateException("Pedido já finalizado e não pode ser alterado.");
        }

        StatusPedido status = StatusPedido.valueOf(novoStatus.toUpperCase());
        pedido.setStatus(status);
        repository.save(pedido);

        if (status == StatusPedido.FINALIZADO) {
            PedidoFinalizadoEvent event = new PedidoFinalizadoEvent(
                    pedido.getCodigo(),
                    pedido.getNomeCliente(),
                    pedido.getProdutoId(),
                    pedido.getQuantidade(),
                    pedido.getPrecoUnitario(),
                    pedido.getTotal()
            );
            pedidoProducer.enviarPedidoFinalizado(event);
            log.info("📦 Evento PedidoFinalizadoEvent publicado para pedido ID={}", pedido.getId());
        }
    }
}
