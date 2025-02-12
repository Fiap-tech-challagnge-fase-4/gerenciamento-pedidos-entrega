package br.com.fiap.entrega.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.entrega.enums.StatusEntrega;
import br.com.fiap.entrega.mapper.EntregaMapper;
import br.com.fiap.entrega.model.Entrega;
import br.com.fiap.entrega.model.dto.EntregaRequestDTO;
import br.com.fiap.entrega.service.EntregaService;

public class EntregaControllerTest {

    private MockMvc mockMvc;

    private AutoCloseable openMocks;

    @Mock
    private EntregaService entregaService;

    private EntregaController entregaController;

    @BeforeEach
    void setup() {
        openMocks = MockitoAnnotations.openMocks(this);

        EntregaMapper entregaMapper = new EntregaMapper();
        entregaController = new EntregaController(entregaService, entregaMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(entregaController).build();
    }

    @AfterEach
    void teardown() throws Exception {
        openMocks.close();
    }

    @Test
    void devePermitirCriarEntrega() throws Exception {
        // Arrange
        EntregaRequestDTO request = gerarUmaEntregaRequestDTO();
        Entrega response = gerarUmaEntrega();

        when(entregaService.criarEntrega(any(Entrega.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/entregas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.pedidoid").value(222))
                .andExpect(jsonPath("$.clienteid").value(123))
                .andExpect(jsonPath("$.dadosentrega").value("Rua A, 25, Jardim das flores, são paulo, SP"))
                .andExpect(jsonPath("$.cep").value("02897-789"))
                .andExpect(jsonPath("$.dataprevistaentrega").isNotEmpty())
                .andExpect(jsonPath("$.dataenvio").isNotEmpty())
                .andExpect(jsonPath("$.dataentrega").isNotEmpty())
                .andExpect(jsonPath("$.statusentrega").value("PENDENTE"))
                .andExpect(jsonPath("$.entregador").value("SP TRANSPORTES"));
    }

    @Test
    void devePermitirListarEntregas() throws Exception {

        // Arrange
        List<Entrega> entregas = Arrays.asList(
                new Entrega(1,
                        555,
                        879,
                        "Rua A, 245, Jardim das flores, São Paulo, SP",
                        "asd897-789",
                        LocalDateTime.now().plusDays(5),
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        StatusEntrega.PENDENTE,
                        "SP TRANSPORTES"),
                new Entrega(2,
                        333,
                        123,
                        "Rua B, 25, Jardim das flores, São Paulo, SP",
                        "99897-789",
                        LocalDateTime.now().plusDays(5),
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        StatusEntrega.PENDENTE,
                        "SP TRANSPORTES"));
        when(entregaService.listarEntregas()).thenReturn(entregas);

        // Act & Assert
        // Act & Assert
        mockMvc.perform(get("/api/entregas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].pedidoid").value(555))
                .andExpect(jsonPath("$[0].clienteid").value(879))
                .andExpect(jsonPath("$[0].dadosentrega").value("Rua A, 245, Jardim das flores, São Paulo, SP"))
                .andExpect(jsonPath("$[0].cep").value("asd897-789"))
                .andExpect(jsonPath("$[0].dataprevistaentrega").isNotEmpty())
                .andExpect(jsonPath("$[0].dataenvio").isNotEmpty())
                .andExpect(jsonPath("$[0].dataentrega").isNotEmpty())
                .andExpect(jsonPath("$[0].statusentrega").value("PENDENTE"))
                .andExpect(jsonPath("$[0].entregador").value("SP TRANSPORTES"))
                .andExpect(jsonPath("$[1].pedidoid").value(333))
                .andExpect(jsonPath("$[1].clienteid").value(123))
                .andExpect(jsonPath("$[1].dadosentrega").value("Rua B, 25, Jardim das flores, São Paulo, SP"))
                .andExpect(jsonPath("$[1].cep").value("99897-789"))
                .andExpect(jsonPath("$[1].dataprevistaentrega").isNotEmpty())
                .andExpect(jsonPath("$[1].dataenvio").isNotEmpty())
                .andExpect(jsonPath("$[1].dataentrega").isNotEmpty())
                .andExpect(jsonPath("$[1].statusentrega").value("PENDENTE"))
                .andExpect(jsonPath("$[1].entregador").value("SP TRANSPORTES"));
    }

    @Test
    void devePermitirFinalizarEntrega() throws Exception {
        // Arrange
        Entrega response = gerarUmaEntrega();

        // Act & Assert
        mockMvc.perform(put("/api/entregas/finalizar/{id}", response.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void devePermitirObterEntrega() throws Exception {

        // Arrange
        Entrega response = gerarUmaEntrega();
        EntregaRequestDTO request = gerarUmaEntregaRequestDTO();
        when(entregaService.obterEntrega(anyInt())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                get("/api/entregas/1").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.pedidoid").value(222))
                .andExpect(jsonPath("$.clienteid").value(123))
                .andExpect(jsonPath("$.dadosentrega").value("Rua A, 25, Jardim das flores, são paulo, SP"))
                .andExpect(jsonPath("$.cep").value("02897-789"))
                .andExpect(jsonPath("$.dataprevistaentrega").isNotEmpty())
                .andExpect(jsonPath("$.dataenvio").isNotEmpty())
                .andExpect(jsonPath("$.dataentrega").isNotEmpty())
                .andExpect(jsonPath("$.statusentrega").value("PENDENTE"))
                .andExpect(jsonPath("$.entregador").value("SP TRANSPORTES"));
    }

    @Test
	void devePermitirAtualizarEntrega() throws Exception {

        // Arrange
		Integer id = 1;
		EntregaRequestDTO requestModificado = gerarUmaEntregaRequestDTOModificada();
		Entrega responseAtualizado = gerarUmaEntregaAtualizada();
		when(entregaService.atualizarEntrega(anyInt(), any(Entrega.class))).thenReturn(responseAtualizado);

		// Act & Assert
        mockMvc.perform(put("/api/entregas/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestModificado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pedidoid").value(222))
                .andExpect(jsonPath("$.clienteid").value(123))
                .andExpect(jsonPath("$.dadosentrega").value("Rua XPTO, 25, Vila Nova, são paulo, SP"))
                .andExpect(jsonPath("$.cep").value("11111-789"))
                .andExpect(jsonPath("$.dataprevistaentrega").isNotEmpty())
                .andExpect(jsonPath("$.dataenvio").isNotEmpty())
                .andExpect(jsonPath("$.dataentrega").isNotEmpty())
                .andExpect(jsonPath("$.statusentrega").value("PENDENTE"))
                .andExpect(jsonPath("$.entregador").value("FIAP Transportes"));
	}

    @Test
	void devePermitirExcluirEntrega() throws Exception {

        // Arrange
		Integer id = 1;
        doNothing().when(entregaService).excluirEntrega(id);

		// Act & Assert
        mockMvc.perform(delete("/api/entregas/{id}", id))
                .andExpect(status().isOk());
	}

    @Test
    void devePermitirAgruparEntregaPendentePorCEP() throws Exception {

        // Arrange
        List<Entrega> entregas = Arrays.asList(
                new Entrega(1,
                        555,
                        879,
                        "Rua A, 245, Jardim das flores, São Paulo, SP",
                        "12897-789",
                        LocalDateTime.now().plusDays(5),
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        StatusEntrega.PENDENTE,
                        "SP TRANSPORTES"),
                new Entrega(2,
                        333,
                        123,
                        "Rua B, 25, Jardim das flores, São Paulo, SP",
                        "12897-789",
                        LocalDateTime.now().plusDays(5),
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        StatusEntrega.PENDENTE,
                        "SP TRANSPORTES"));
        when(entregaService.agruparEntregaPendentePorCEP("128")).thenReturn(entregas);

        // Act & Assert
        // Act & Assert
        mockMvc.perform(get("/api/entregas/agrupar/128"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].pedidoid").value(555))
                .andExpect(jsonPath("$[0].clienteid").value(879))
                .andExpect(jsonPath("$[0].dadosentrega").value("Rua A, 245, Jardim das flores, São Paulo, SP"))
                .andExpect(jsonPath("$[0].cep").value("12897-789"))
                .andExpect(jsonPath("$[0].dataprevistaentrega").isNotEmpty())
                .andExpect(jsonPath("$[0].dataenvio").isNotEmpty())
                .andExpect(jsonPath("$[0].dataentrega").isNotEmpty())
                .andExpect(jsonPath("$[0].statusentrega").value("PENDENTE"))
                .andExpect(jsonPath("$[0].entregador").value("SP TRANSPORTES"))
                .andExpect(jsonPath("$[1].pedidoid").value(333))
                .andExpect(jsonPath("$[1].clienteid").value(123))
                .andExpect(jsonPath("$[1].dadosentrega").value("Rua B, 25, Jardim das flores, São Paulo, SP"))
                .andExpect(jsonPath("$[1].cep").value("12897-789"))
                .andExpect(jsonPath("$[1].dataprevistaentrega").isNotEmpty())
                .andExpect(jsonPath("$[1].dataenvio").isNotEmpty())
                .andExpect(jsonPath("$[1].dataentrega").isNotEmpty())
                .andExpect(jsonPath("$[1].statusentrega").value("PENDENTE"))
                .andExpect(jsonPath("$[1].entregador").value("SP TRANSPORTES"));
    }

    private EntregaRequestDTO gerarUmaEntregaRequestDTO() {
        return new EntregaRequestDTO(9999, 1);
    }

    public static String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private Entrega gerarUmaEntrega() {
        return new Entrega(1,
                222,
                123,
                "Rua A, 25, Jardim das flores, são paulo, SP",
                "02897-789",
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatusEntrega.PENDENTE,
                "SP TRANSPORTES");
    }

    private EntregaRequestDTO gerarUmaEntregaRequestDTOModificada() {
		return new EntregaRequestDTO(222, 123);
	}

    private Entrega gerarUmaEntregaAtualizada() {
		return new Entrega(1,
                222,
                123,
                "Rua XPTO, 25, Vila Nova, são paulo, SP",
                "11111-789",
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatusEntrega.PENDENTE,
                "FIAP Transportes");
	}

}
