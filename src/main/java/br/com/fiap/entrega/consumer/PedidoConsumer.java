package br.com.fiap.entrega.consumer;

import br.com.fiap.entrega.model.Entrega;
import br.com.fiap.entrega.model.Pedido;
import br.com.fiap.entrega.service.impl.EntregaServiceImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class PedidoConsumer {

    private final EntregaServiceImpl entregaService;
 
    public PedidoConsumer(EntregaServiceImpl entregaService) {
        this.entregaService = entregaService;
    }

    @Bean
    public Consumer<Message<Pedido>> pedidoEntregaQueue() {
        return message -> {
            Pedido pedido = message.getPayload();
            Entrega entrega = new Entrega(pedido.getId(), pedido.getClienteid());
            entregaService.criarEntrega(entrega);
        };
    }
}
