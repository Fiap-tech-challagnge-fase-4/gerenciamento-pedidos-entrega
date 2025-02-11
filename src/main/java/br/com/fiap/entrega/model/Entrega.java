package br.com.fiap.entrega.model;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.entrega.enums.StatusEntrega;
import lombok.Getter;

@Getter
public class Entrega {

    private Integer id;
    private Integer pedidoid;
    private Integer clienteid;
    private String dadosentrega;
    private String cep;
    private LocalDateTime dataenvio;
    private LocalDateTime dataprevistaentrega;
    private LocalDateTime dataentrega;
    private StatusEntrega statusentrega;
    private String entregador;

    public Entrega(Integer id, Integer pedidoid, Integer clienteid, String dadosentrega, String cep,
            LocalDateTime dataenvio, LocalDateTime dataprevistaentrega, LocalDateTime dataentrega,
            StatusEntrega statusentrega, String entregador) {
        this.id = id;
        this.pedidoid = pedidoid;
        this.clienteid = clienteid;
        this.dadosentrega = dadosentrega;
        this.cep = cep;
        this.dataenvio = dataenvio;
        this.dataprevistaentrega = dataprevistaentrega;
        this.dataentrega = dataentrega;
        this.statusentrega = statusentrega;
        this.entregador = entregador;
    }

    public Entrega(Integer pedidoid, Integer clienteid) {
        this.pedidoid = pedidoid;
        this.clienteid = clienteid;
        this.dataprevistaentrega = LocalDateTime.now().plusDays(5); // Previsão default de 5 dias - se der tempo, usar
                                                                    // API externa e calcular com base em distancia!
        this.statusentrega = StatusEntrega.PENDENTE;
        this.entregador = "TRANSPORTES FIAP"; // Se der tempo efetuar um cadastro de entregadores
    }

    public boolean isPendente() {
        return statusentrega == StatusEntrega.PENDENTE;
    }

    public void atribuirDadosComplementaresDoCliente(RestTemplate restTemplate, ObjectMapper objectMapper,
            String clienteURL) {

        try {

            ResponseEntity<String> response = restTemplate.getForEntity(
                    clienteURL + "{id}",
                    String.class,
                    this.clienteid);

            // Acessa outro microserviço para capturar os dados de entrega do cliente
            if (!response.hasBody() || response.getBody().isEmpty()
                    || response.getStatusCode() == HttpStatus.NOT_FOUND) {
                return;
            } else {

                JsonNode produtoJson = objectMapper.readTree(response.getBody());

                String dadosentrega = "Nome: " + produtoJson.get("nome").asText() +
                        " - Telefone: " + produtoJson.get("telefone").asText() +
                        " - Endereço: " + produtoJson.get("endereco").asText() +
                        " - CEP: " + produtoJson.get("cep").asText();

                this.dadosentrega = dadosentrega;
                this.cep = produtoJson.get("cep").asText();

            }
        } catch (Exception e) {
            // throw new EntityNotFoundException("Não foi possível carregar os dados
            // complementares do cliente.");
            // Apenas logar a exception, não faz sentido parar o processo por conta de uma
            // falha de dados complementares
        }

    }
}
