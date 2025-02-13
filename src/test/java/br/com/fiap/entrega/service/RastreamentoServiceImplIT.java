package br.com.fiap.entrega.service;


import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import br.com.fiap.entrega.model.Rastreamento;
import br.com.fiap.entrega.model.entity.RastreamentoEntity;
import br.com.fiap.entrega.repository.RastreamentoRepository;

@AutoConfigureTestDatabase
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RastreamentoServiceImplIT {

    @LocalServerPort
	private int port;
	
	@Autowired
	private RastreamentoService rastreamentoService;
	
    @Autowired
    private RastreamentoRepository rastreamentoRepository;	

    @Test
	void devePermitirCriarRastreamento() {
		// Arrange
		Rastreamento rastreamentoRequest = gerarUmRastreamento();
        
		// Act
		Rastreamento rastreamentoResponse = rastreamentoService.criarRastreamento(rastreamentoRequest);
		
		// Assert
		assertThat(rastreamentoResponse).isInstanceOf(Rastreamento.class).isNotNull();
		assertThat(rastreamentoResponse.getId()).isNotNull();
		assertThat(rastreamentoResponse.getPedidoid()).isEqualTo(rastreamentoRequest.getPedidoid());
		assertThat(rastreamentoResponse.getLatitude()).isEqualTo(rastreamentoRequest.getLatitude());
		assertThat(rastreamentoResponse.getLongitude()).isEqualTo(rastreamentoRequest.getLongitude());
	}

	@Test
	void devePermitirlistarRastreamentoPorPedidoId() {
		// Arrange
		Integer pedidoId = 1;
		rastreamentoRepository.save(new RastreamentoEntity(1, pedidoId, "123456", "-78910", LocalDateTime.now()));
		rastreamentoRepository.save(new RastreamentoEntity(2, pedidoId, "78952", "-98910", LocalDateTime.now()));
		
		// Act
		List<Rastreamento> listaRastreamentos = rastreamentoService.listarRastreamentoPorPedidoId(pedidoId);
		
		// Assert
		assertThat(listaRastreamentos)
	    .isNotEmpty()
	    .hasSizeGreaterThanOrEqualTo(2)
		.allSatisfy(rastreamento -> assertThat(rastreamento).isNotNull());
	}

	

	private Rastreamento gerarUmRastreamento() {
		return new Rastreamento(1, 1, "123456", "-78910", LocalDateTime.now());
	}
}
