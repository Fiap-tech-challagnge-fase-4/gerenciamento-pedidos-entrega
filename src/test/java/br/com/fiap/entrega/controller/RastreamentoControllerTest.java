package br.com.fiap.entrega.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import br.com.fiap.entrega.mapper.RastreamentoMapper;
import br.com.fiap.entrega.model.Rastreamento;
import br.com.fiap.entrega.model.dto.RastreamentoRequestDTO;
import br.com.fiap.entrega.service.RastreamentoService;


public class RastreamentoControllerTest {
    
    private MockMvc mockMvc;

    private AutoCloseable openMocks;

    @Mock
    private RastreamentoService rastreamentoService;

    private RastreamentoController rastreamentoController;

    @BeforeEach
    void setup() {
        openMocks = MockitoAnnotations.openMocks(this);

        RastreamentoMapper rastreamentoMapper = new RastreamentoMapper();
        rastreamentoController = new RastreamentoController(rastreamentoService, rastreamentoMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(rastreamentoController).build();
    }

    @AfterEach
    void teardown() throws Exception {
        openMocks.close();
    }

    @Test
    void devePermitirCriarRastreamento() throws Exception {
        // Arrange
        RastreamentoRequestDTO request = gerarUmRastreamentoRequestDTO();
        Rastreamento response = gerarUmRastreamento();

        when(rastreamentoService.criarRastreamento(any(Rastreamento.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/rastreamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.pedidoid").value(123))
                .andExpect(jsonPath("$.latitude").value("1234"))
                .andExpect(jsonPath("$.longitude").value("-5678"))
                .andExpect(jsonPath("$.dataHora").isNotEmpty());
    }

    @Test
    void devePermitirListarRastreamentos() throws Exception {

        // Arrange
        List<Rastreamento> rastreamentos = Arrays.asList(
                new Rastreamento(1, 123, "1234", "-5678", LocalDateTime.now()),
                new Rastreamento(2, 123, "1288", "-5674", LocalDateTime.now()));
        when(rastreamentoService.listarRastreamentoPorPedidoId(123)).thenReturn(rastreamentos);

        // Act & Assert
        mockMvc.perform(get("/api/rastreamentos/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].pedidoid").value(123))
                .andExpect(jsonPath("$[0].latitude").value("1234"))
                .andExpect(jsonPath("$[0].longitude").value("-5678"))
                .andExpect(jsonPath("$[0].dataHora").isNotEmpty())
                .andExpect(jsonPath("$[1].pedidoid").value(123))
                .andExpect(jsonPath("$[1].latitude").value("1288"))
                .andExpect(jsonPath("$[1].longitude").value("-5674"))
                .andExpect(jsonPath("$[1].dataHora").isNotEmpty());
    }

    private RastreamentoRequestDTO gerarUmRastreamentoRequestDTO() {
        return new RastreamentoRequestDTO(1, "1234", "-5678");
    }

    private Rastreamento gerarUmRastreamento() {
        return new Rastreamento(1, 123, "1234", "-5678", LocalDateTime.now());
    }

    public static String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
