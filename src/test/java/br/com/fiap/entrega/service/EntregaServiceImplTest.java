package br.com.fiap.entrega.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.fiap.entrega.enums.StatusEntrega;
import br.com.fiap.entrega.mapper.EntregaMapper;
import br.com.fiap.entrega.model.Entrega;
import br.com.fiap.entrega.model.entity.EntregaEntity;
import br.com.fiap.entrega.repository.EntregaRepository;
import br.com.fiap.entrega.service.impl.EntregaServiceImpl;


public class EntregaServiceImplTest {

    @Mock
    private EntregaRepository entregaRepository;
    
    private EntregaServiceImpl entregaServiceImpl;

    private AutoCloseable openMocks;

    @BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		EntregaMapper entregaMapper = new EntregaMapper();
		entregaServiceImpl = new EntregaServiceImpl(entregaMapper, entregaRepository);
	}

	@AfterEach
	void teardown() throws Exception {
		openMocks.close();
	}

    @Test
	void devePermitirListarEntregas() {
		// Arrange
		List<EntregaEntity> listaEntregas = gerarListaDeEntregaEntity();
		when(entregaRepository.findAll()).thenReturn(listaEntregas);
	
		// Act
		List<Entrega> listaObtida = entregaServiceImpl.listarEntregas();
	
		// Assert
		verify(entregaRepository, times(1)).findAll();
		assertThat(listaObtida)
	    .hasSize(3)
	    .allSatisfy(entrega -> assertThat(entrega).isNotNull().isInstanceOf(Entrega.class));
	}

    @Test
	void devePermitirCriarEntrega() {
		// Arrange
		Entrega entregaRequest = gerarUmaEntrega();
		EntregaEntity entregaEntity = gerarUmaEntregaEntity(1);
		when(entregaRepository.save(any(EntregaEntity.class))).thenReturn(entregaEntity);
        
		// Act
		Entrega entregaResponse = entregaServiceImpl.criarEntrega(entregaRequest);
		
		// Assert
		verify(entregaRepository, times(1)).save(any(EntregaEntity.class));
		assertThat(entregaResponse).isInstanceOf(Entrega.class).isNotNull();
		assertThat(entregaRequest.getPedidoid()).isEqualTo(entregaRequest.getPedidoid());
        assertThat(entregaRequest.getClienteid()).isEqualTo(entregaRequest.getClienteid());
        assertThat(entregaResponse.getStatusentrega()).isEqualTo(entregaRequest.getStatusentrega());
    }

    @Test
	void devePermitirFinalizarEntrega() {
		//Arrange
        Integer entregaId = 1;
        EntregaEntity entregaEntity = gerarUmaEntregaEntity(entregaId);
        when(entregaRepository.findById(entregaId)).thenReturn(Optional.of(entregaEntity));
		when(entregaRepository.save(any(EntregaEntity.class))).thenReturn(entregaEntity);
        
		// Act
		entregaServiceImpl.finalizarEntrega(entregaId);
		
		// Assert
		verify(entregaRepository, times(1)).findById(entregaId);
		verify(entregaRepository, times(1)).save(any(EntregaEntity.class));
		assertThat(entregaEntity.getStatusentrega()).isEqualTo(StatusEntrega.ENTREGUE);
	}

    @Test
	void devePermitirObterEntrega() {
		// Arrange
        Integer id = 1;
		EntregaEntity entregaEntity = gerarUmaEntregaEntity(id);
		when(entregaRepository.findById(id)).thenReturn(Optional.of(entregaEntity));
		
		// Act
		Entrega entregaObtido = entregaServiceImpl.obterEntrega(entregaEntity.getId());
		
		// Assert
		verify(entregaRepository, times(1)).findById(anyInt());
		assertThat(entregaObtido).isInstanceOf(Entrega.class).isNotNull();
		assertThat(entregaObtido.getId()).isEqualTo(entregaEntity.getId());
		assertThat(entregaObtido.getPedidoid()).isEqualTo(entregaEntity.getPedidoid());
        assertThat(entregaObtido.getClienteid()).isEqualTo(entregaEntity.getClienteid());
        assertThat(entregaObtido.getStatusentrega()).isEqualTo(entregaEntity.getStatusentrega());
	}

    @Test
	void devePermitirAtualizarUmEntrega() {
		// Arrange
		Entrega entregaSemModificacao = gerarUmaEntrega();
		EntregaEntity entregaEntity = gerarUmaEntregaEntity(1);
		Entrega entregaModificado = new Entrega(null,
                                    222,
                                    123,
                                    "Rua XPTO, 25, Jardim das flores, são paulo, SP",
                                    "67897-789",
                                    LocalDateTime.now().plusDays(5),
                                    LocalDateTime.now(),
                                    LocalDateTime.now(),
                                    StatusEntrega.PENDENTE,
                                    "CARRETA FURACAO TRANSPORTES");
		
		when(entregaRepository.findById(anyInt())).thenReturn(Optional.of(entregaEntity));
		when(entregaRepository.save(any(EntregaEntity.class))).thenReturn(entregaEntity);
		
		// Act
		Entrega entregaObtido = entregaServiceImpl.atualizarEntrega(1, entregaModificado);
		
		// Assert
		verify(entregaRepository, times(1)).findById(anyInt());
		verify(entregaRepository, times(1)).save(any(EntregaEntity.class));
		assertThat(entregaObtido).isInstanceOf(Entrega.class).isNotNull();
		assertThat(entregaObtido.getId()).isEqualTo(entregaSemModificacao.getId());
		assertThat(entregaObtido.getPedidoid()).isEqualTo(entregaSemModificacao.getPedidoid());
        assertThat(entregaObtido.getClienteid()).isEqualTo(entregaSemModificacao.getClienteid());
        assertThat(entregaObtido.getStatusentrega()).isEqualTo(entregaSemModificacao.getStatusentrega());
        assertThat(entregaObtido.getDadosentrega()).isNotEqualTo(entregaSemModificacao.getDadosentrega());
        assertThat(entregaObtido.getCep()).isNotEqualTo(entregaSemModificacao.getCep());
        assertThat(entregaObtido.getEntregador()).isNotEqualTo(entregaSemModificacao.getEntregador());
	}

    @Test
	void devePermitirExcluirEntrega() {
		// Arrange
		Integer id = 1;
		EntregaEntity entregaEntity = gerarUmaEntregaEntity(id);
		when(entregaRepository.findById(id)).thenReturn(Optional.of(entregaEntity));
		when(entregaRepository.save(any(EntregaEntity.class))).thenReturn(entregaEntity);

		// Act
		entregaServiceImpl.excluirEntrega(id);
		
		// Assert
		verify(entregaRepository, times(1)).findById(anyInt());
		verify(entregaRepository, times(1)).save(any(EntregaEntity.class));
		assertThat(entregaEntity.getStatusentrega()).isEqualTo(StatusEntrega.CANCELADA);
	}

    @Test
	void devePermitirAgruparEntregaPendentePorCEP() {
		// Arrange
		List<EntregaEntity> listaEntregas = gerarListaDeEntregaEntity();
		when(entregaRepository.findByCepStartingWithAndStatusentrega("123", StatusEntrega.PENDENTE)).thenReturn(listaEntregas);
	
		// Act
		List<Entrega> listaObtida = entregaServiceImpl.agruparEntregaPendentePorCEP("123");
	
		// Assert
		verify(entregaRepository, times(1)).findByCepStartingWithAndStatusentrega("123", StatusEntrega.PENDENTE);
		assertThat(listaObtida)
	    .hasSize(3)
	    .allSatisfy(entrega -> assertThat(entrega).isNotNull().isInstanceOf(Entrega.class));
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

    private EntregaEntity gerarUmaEntregaEntity(Integer entregaId)
    {
        return new EntregaEntity(
                entregaId,
                12,
                111,
                "Rua XPTO, 56", 
                "02598-784", 
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatusEntrega.PENDENTE,
                "FIAP transportes");
    }

    private List<EntregaEntity> gerarListaDeEntregaEntity() {
		return Arrays.asList (
            new EntregaEntity(
                1,
                13,
                222,
                "Rua ABC, 56", 
                "12398-784", 
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatusEntrega.PENDENTE,
                "FIAP transportes"),
            new EntregaEntity(
                2,
                12,
                111,
                "Rua XPTO, 56", 
                "12398-854", 
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatusEntrega.PENDENTE,
                "FIAP transportes"),
            new EntregaEntity(
                3,
                67,
                111,
                "Rua ZZZ, 56", 
                "12308-784", 
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatusEntrega.PENDENTE,
                "XPTO transportes"));
	}
}
