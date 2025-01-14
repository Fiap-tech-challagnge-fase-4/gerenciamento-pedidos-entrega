package br.com.fiap.entrega.consumer;

import br.com.fiap.entrega.Service.EntregaService;
import br.com.fiap.entrega.model.Entrega;
import br.com.fiap.entrega.model.Pedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Consumer;

@Service
public class PedidoConsumer {

    @Autowired
    private EntregaService entregaService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${cliente.url}")
    private String clienteURL;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public Consumer<Message<Pedido>> pedidoEntregaQueue() {
        return message -> {
            Pedido pedido = message.getPayload();
            Entrega entrega = new Entrega();
            entrega.setPedidoid(pedido.getId());
            entrega.setClienteid(pedido.getClienteid());
            entrega.setDataprevistaentrega(LocalDateTime.now().plusDays(5)); // Previsão default de 5 dias - Parametrizar, se der tempo, calcular com base em distancia!
            entrega.setStatusentrega("PENDENTE");
            entrega.setTransportadora("TRANSPORTES FIAP"); // Apenas ilustrativo
            entrega.setRastreamentocodigo(UUID.randomUUID().toString());

            ResponseEntity<String> response = restTemplate.getForEntity(
                    clienteURL + "{id}",
                    String.class,
                    pedido.getClienteid());

            // Acessa outro microserviço para capturar os dados de entrega
            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NoSuchElementException("Cliente não encontrado");
            } else {
                try {
                    JsonNode produtoJson = objectMapper.readTree(response.getBody());

                    String dadosentrega = "Nome: " + produtoJson.get("nome").asText() +
                            " - Telefone: " + produtoJson.get("telefone").asText() +
                            " - Endereço: " + produtoJson.get("endereco").asText();

                    entrega.setDadosentrega(dadosentrega);
                } catch (IOException e) {
                    // Tratar erro
                }
            }

            entregaService.criarEntrega(entrega);
        };
    }
}
