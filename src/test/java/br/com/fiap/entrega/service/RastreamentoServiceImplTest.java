package br.com.fiap.entrega.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.fiap.entrega.mapper.RastreamentoMapper;
import br.com.fiap.entrega.model.Rastreamento;
import br.com.fiap.entrega.model.entity.RastreamentoEntity;
import br.com.fiap.entrega.repository.RastreamentoRepository;
import br.com.fiap.entrega.service.impl.RastreamentoServiceImpl;

class RastreamentoServiceImplTest {

    @Mock
    private RastreamentoRepository rastreamentoRepository;
    
    private RastreamentoServiceImpl rastreamentoServiceImpl;

    private AutoCloseable openMocks;

    @BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		RastreamentoMapper rastreamentoMapper = new RastreamentoMapper();
		rastreamentoServiceImpl = new RastreamentoServiceImpl(rastreamentoMapper, rastreamentoRepository);
	}

	@AfterEach
	void teardown() throws Exception {
		openMocks.close();
	}
    
    @Test
	void devePermitirCriarRastreamento() {
		// Arrange
		Rastreamento rastreamentoRequest = gerarUmRastreamento();
		RastreamentoEntity rastreamentoEntity = gerarUmRastreamentoEntity();
		when(rastreamentoRepository.save(any(RastreamentoEntity.class))).thenReturn(rastreamentoEntity);
        
		// Act
		Rastreamento rastreamentoResponse = rastreamentoServiceImpl.criarRastreamento(rastreamentoRequest);
		
		// Assert
		verify(rastreamentoRepository, times(1)).save(any(RastreamentoEntity.class));
		assertThat(rastreamentoResponse).isInstanceOf(Rastreamento.class).isNotNull();
		assertThat(rastreamentoResponse.getPedidoid()).isEqualTo(rastreamentoRequest.getPedidoid());
		assertThat(rastreamentoResponse.getLatitude()).isEqualTo(rastreamentoRequest.getLatitude());
		assertThat(rastreamentoResponse.getLongitude()).isEqualTo(rastreamentoRequest.getLongitude());
	}

    @Test
	void devePermitirListarRastreamentos() {
		// Arrange
        Integer pedidoId = 333;
		List<RastreamentoEntity> listaRastreamentos = gerarListaDeRastreamentoEntity(pedidoId);
		when(rastreamentoRepository.findByPedidoid(pedidoId)).thenReturn(listaRastreamentos);
		
		// Act
		List<Rastreamento> listaObtida = rastreamentoServiceImpl.listarRastreamentoPorPedidoId(pedidoId);
		
		// Assert
		verify(rastreamentoRepository, times(1)).findByPedidoid(pedidoId);
		assertThat(listaObtida)
	    .hasSize(3)
	    .allSatisfy(rastreamento -> assertThat(rastreamento).isNotNull().isInstanceOf(Rastreamento.class));
	}

    private Rastreamento gerarUmRastreamento() {
		return new Rastreamento(1, 1, "123456", "-78910", LocalDateTime.now());
	}

    private RastreamentoEntity gerarUmRastreamentoEntity() {
		return new RastreamentoEntity(1, 1, "123456", "-78910", LocalDateTime.now());
	}

    private List<RastreamentoEntity> gerarListaDeRastreamentoEntity(Integer pedidoId) {
		return  Arrays.asList (
            new RastreamentoEntity(1, pedidoId, "153456", "-88910", LocalDateTime.now()),
			new RastreamentoEntity(1, pedidoId, "163456", "-98910", LocalDateTime.now()),
			new RastreamentoEntity(1, pedidoId, "173456", "-08910", LocalDateTime.now()));
	}
}
