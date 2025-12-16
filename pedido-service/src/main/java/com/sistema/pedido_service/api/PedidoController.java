package com.sistema.pedido_service.api;

import com.sistema.pedido_service.api.dto.PedidoResponse;
import com.sistema.pedido_service.aplicacao.PedidoService;
import com.sistema.pedido_service.dominio.Pedido;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService service;

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listar() {
        List<PedidoResponse> pedidos = service.listarTodos()
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(pedidos);
    }

    @PatchMapping("/{id}/status/{novoStatus}")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @PathVariable String novoStatus) {
        service.atualizarStatus(id, novoStatus);
        return ResponseEntity.noContent().build();
    }

    private PedidoResponse toResponse(Pedido pedido) {
        return PedidoResponse.builder()
                .id(pedido.getId())
                .codigo(pedido.getCodigo())
                .nomeCliente(pedido.getNomeCliente())
                .produto(pedido.getProduto())
                .quantidade(pedido.getQuantidade())
                .precoUnitario(pedido.getPrecoUnitario())
                .total(pedido.getTotal())
                .status(pedido.getStatus())
                .build();
    }
}
