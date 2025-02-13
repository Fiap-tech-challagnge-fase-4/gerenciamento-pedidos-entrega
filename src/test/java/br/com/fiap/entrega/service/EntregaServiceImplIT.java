package br.com.fiap.entrega.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import br.com.fiap.entrega.enums.StatusEntrega;
import br.com.fiap.entrega.model.Entrega;
import br.com.fiap.entrega.model.entity.EntregaEntity;
import br.com.fiap.entrega.repository.EntregaRepository;
import br.com.fiap.entrega.exceptions.EntityNotFoundException;

@AutoConfigureTestDatabase
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EntregaServiceImplIT {

    
    @LocalServerPort
	private int port;
	
	@Autowired
	private EntregaService entregaService;
	
    @Autowired
    private EntregaRepository entregaRepository;	

    @Test
	void devePermitirCriarEntrega() {
		// Arrange
		Entrega entregaRequest = gerarUmaEntrega();
        
		// Act
		Entrega entregaResponse = entregaService.criarEntrega(entregaRequest);
		
		// Assert
		assertThat(entregaResponse).isInstanceOf(Entrega.class).isNotNull();
		assertThat(entregaResponse.getId()).isNotNull();
		assertThat(entregaResponse.getPedidoid()).isEqualTo(entregaRequest.getPedidoid());
        assertThat(entregaResponse.getStatusentrega()).isEqualTo(entregaRequest.getStatusentrega());
        assertThat(entregaResponse.getEntregador()).isEqualTo(entregaRequest.getEntregador());
        assertThat(entregaResponse.getCep()).isEqualTo(entregaRequest.getCep());
        assertThat(entregaResponse.getDadosentrega()).isEqualTo(entregaRequest.getDadosentrega());
		assertThat(entregaResponse.getEntregador()).isEqualTo(entregaRequest.getEntregador());
    }

    @Test
	void devePermitirListarEntregas() {
		// Arrange
		entregaRepository.save(
            new EntregaEntity(
                1,
                12,
                111,
                "Rua XPTO, 56", 
                "02598-784", 
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatusEntrega.PENDENTE,
                "FIAP transportes"));
        entregaRepository.save(
            new EntregaEntity(
                2,
                15,
                123,
                "Rua ABCD, 987", 
                "12978-784", 
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatusEntrega.PENDENTE,
                "FIAP transportes"));
		
		// Act
		List<Entrega> listaEntregas = entregaService.listarEntregas();
		
		// Assert
		assertThat(listaEntregas)
	    .isNotEmpty()
	    .hasSizeGreaterThanOrEqualTo(2)
		.allSatisfy(entrega -> assertThat(entrega).isNotNull());
	}

    @Test
	void devePermitirFinalizarEntrega() {
		//Arrange
        EntregaEntity entrega = gerarUmaEntregaEntity();
        EntregaEntity entregaEntity = entregaRepository.save(entrega);
        
		// Act
		entregaService.finalizarEntrega(entregaEntity.getId());
		
		// Assert
		EntregaEntity entregaFinalizada = entregaRepository.findById(entregaEntity.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Entrega não encontrada"));
		assertThat(entregaFinalizada.getStatusentrega()).isEqualTo(StatusEntrega.ENTREGUE);
	}

    @Test
	void naoDevePermitirFinalizarEntrega() {
		//Arrange
        EntregaEntity entrega = gerarUmaEntregaEntity();
        entrega.setStatusentrega(StatusEntrega.ENTREGUE);
        EntregaEntity entregaEntity = entregaRepository.save(entrega);
        
		// Act 
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            entregaService.finalizarEntrega(entregaEntity.getId());
        });

        //Assert  
        assertEquals("A entrega não pode ser mais modificada.", exception.getMessage());
	}

    @Test
	void devePermitirObterEntrega() {
		// Arrange
		EntregaEntity entregaEntity = entregaRepository.save(gerarUmaEntregaEntity());
		
		// Act
		Entrega entregaResponse = entregaService.obterEntrega(entregaEntity.getId());
		
		// Assert
		assertThat(entregaResponse).isInstanceOf(Entrega.class).isNotNull();
		assertThat(entregaResponse.getId()).isNotNull();
		assertThat(entregaResponse.getPedidoid()).isEqualTo(entregaEntity.getPedidoid());
        assertThat(entregaResponse.getStatusentrega()).isEqualTo(entregaEntity.getStatusentrega());
        assertThat(entregaResponse.getEntregador()).isEqualTo(entregaEntity.getEntregador());
        assertThat(entregaResponse.getCep()).isEqualTo(entregaEntity.getCep());
        assertThat(entregaResponse.getDadosentrega()).isEqualTo(entregaEntity.getDadosentrega());
		assertThat(entregaResponse.getEntregador()).isEqualTo(entregaEntity.getEntregador());
	}

    @Test
	void devePermitirAtualizarEntrega() {
		// Arrange
		EntregaEntity entregaSemModificacao = gerarUmaEntregaEntity();
		EntregaEntity entregaModificado = entregaRepository.save(gerarUmaEntregaEntity());
		entregaModificado.setCep("12345-789");
		entregaModificado.setDadosentrega("Rua saguairu, 456");
		entregaModificado.setEntregador("Carreta Furacao");
		Integer id = entregaModificado.getId();
		
		Entrega entregaModificadoRequest = new Entrega(null, entregaModificado.getPedidoid(),
        entregaModificado.getClienteid(), entregaModificado.getDadosentrega(), entregaModificado.getCep(),
        entregaModificado.getDataenvio(), entregaModificado.getDataprevistaentrega(), entregaModificado.getDataentrega(),
        entregaModificado.getStatusentrega(), entregaModificado.getEntregador());
		
		// Act
		Entrega entregaObtido = entregaService.atualizarEntrega(id, entregaModificadoRequest);
		
		// Assert
		assertThat(entregaObtido).isInstanceOf(Entrega.class).isNotNull();
		assertThat(entregaObtido.getId()).isEqualTo(id);
		assertThat(entregaObtido.getPedidoid()).isEqualTo(entregaSemModificacao.getPedidoid());
        assertThat(entregaObtido.getStatusentrega()).isEqualTo(entregaSemModificacao.getStatusentrega());
        assertThat(entregaObtido.getEntregador()).isNotEqualTo(entregaSemModificacao.getEntregador());
        assertThat(entregaObtido.getCep()).isNotEqualTo(entregaSemModificacao.getCep());
        assertThat(entregaObtido.getDadosentrega()).isNotEqualTo(entregaSemModificacao.getDadosentrega());
	}
    
    @Test
	void naoDevePermitirAtualizarEntrega() {
		// Arrange
        EntregaEntity entity = gerarUmaEntregaEntity();
        entity.setStatusentrega(StatusEntrega.ENTREGUE);
		EntregaEntity entregaModificado = entregaRepository.save(entity);
		entregaModificado.setCep("12345-789");
		entregaModificado.setDadosentrega("Rua saguairu, 456");
		entregaModificado.setEntregador("Carreta Furacao");
        Integer id = entregaModificado.getId();
		
		Entrega entregaModificadoRequest = new Entrega(null, entregaModificado.getPedidoid(),
        entregaModificado.getClienteid(), entregaModificado.getDadosentrega(), entregaModificado.getCep(),
        entregaModificado.getDataenvio(), entregaModificado.getDataprevistaentrega(), entregaModificado.getDataentrega(),
        entregaModificado.getStatusentrega(), entregaModificado.getEntregador());
		
		// Act 
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            entregaService.atualizarEntrega(id, entregaModificadoRequest);
        });

        //Assert  
        assertEquals("A entrega não pode ser mais atualizada.", exception.getMessage());
	}

    @Test
	void devePermitirExcluirEntrega() {
		// Arrange
		EntregaEntity entregaEntity = entregaRepository.save(gerarUmaEntregaEntity());

		// Act
		entregaService.excluirEntrega(entregaEntity.getId());

		// Assert
		EntregaEntity entregaAtualizado = entregaRepository.findById(entregaEntity.getId())
				.orElseThrow(() -> new EntityNotFoundException("Entrega não encontrado"));
		assertThat(entregaAtualizado.getStatusentrega()).isEqualTo(StatusEntrega.CANCELADA);
	}

    @Test
	void naoDevePermitirExcluirEntrega() {
		// Arrange
        EntregaEntity entity = gerarUmaEntregaEntity();
        entity.setStatusentrega(StatusEntrega.ENTREGUE);
		EntregaEntity entregaEntity = entregaRepository.save(entity);
        
		// Act 
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            entregaService.excluirEntrega(entregaEntity.getId());
        });

        //Assert  
        assertEquals("A entrega não pode ser mais modificada.", exception.getMessage());
	}

    @Test
	void devePermitirAgruparEntregaPendentePorCEP() {
		// Arrange
		entregaRepository.save(
            new EntregaEntity(
                1,
                12,
                111,
                "Rua XPTO, 56", 
                "12398-784", 
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatusEntrega.PENDENTE,
                "FIAP transportes"));
        entregaRepository.save(
            new EntregaEntity(
                2,
                15,
                123,
                "Rua ABCD, 987", 
                "12378-784", 
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatusEntrega.PENDENTE,
                "ABC transportes"));
        entregaRepository.save(
            new EntregaEntity(
                3,
                15,
                456,
                "Rua XYZ, 987", 
                "22278-784", 
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                StatusEntrega.PENDENTE,
                "XYZ transportes"));
		
		// Act
		List<Entrega> listaEntregas = entregaService.agruparEntregaPendentePorCEP("123");
		
		// Assert
		assertThat(listaEntregas)
	    .isNotEmpty()
	    .hasSize(2)
		.allSatisfy(entrega -> assertThat(entrega).isNotNull());
	}

    private EntregaEntity gerarUmaEntregaEntity()
    {
        return new EntregaEntity(
                null,
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
}
