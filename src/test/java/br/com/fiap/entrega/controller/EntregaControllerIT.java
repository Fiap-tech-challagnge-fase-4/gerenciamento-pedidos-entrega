package br.com.fiap.entrega.controller;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasKey;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import br.com.fiap.entrega.enums.StatusEntrega;
import br.com.fiap.entrega.model.dto.EntregaRequestDTO;
import br.com.fiap.entrega.model.entity.EntregaEntity;
import br.com.fiap.entrega.repository.EntregaRepository;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;

@AutoConfigureTestDatabase
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EntregaControllerIT {

    @LocalServerPort
	private int port;

    @Autowired
    private EntregaRepository entregaRepository;

	@BeforeEach
	public void setup() {
	    RestAssured.port = port;
	    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
	}

	@Test
	void devePermitirCriarEntrega() {
		// Arrange
		EntregaRequestDTO request = gerarUmaEntregaRequestDTO();

		// Act & Assert
		given().filter(new AllureRestAssured()).contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(request)
			.when().post("/api/entrega")
			.then().statusCode(HttpStatus.CREATED.value())
			.body(matchesJsonSchemaInClasspath("./schemas/EntregaSchema.json"))
			.body("$", hasKey("pedidoid"))
			.body("$", hasKey("clienteid"))
			.body("$", hasKey("dadosentrega"))
			.body("$", hasKey("cep"))
			.body("$", hasKey("dataenvio"))
			.body("$", hasKey("dataprevistaentrega"))
			.body("$", hasKey("dataentrega"))
			.body("$", hasKey("statusentrega"))
			.body("$", hasKey("entregador"))
			.body("pedidoid", greaterThan(0))
			.body("clienteid", greaterThan(0));
	}

	@Test
	void devePermitirListarEntregas() {
		// Arrange
		entregaRepository.save(new EntregaEntity(
			1, 999, 123, "Rua boa saguairu, 33, sao paulo", "02488-789", 
			LocalDateTime.now(), LocalDateTime.now().plusDays(5), null, StatusEntrega.PENDENTE, "FIAP TRANSPORTADORA" 
		));

		entregaRepository.save(new EntregaEntity(
			2, 777, 222, "Rua XPTO, 33, sao paulo", "02488-789", 
			LocalDateTime.now(), LocalDateTime.now().plusDays(5), null, StatusEntrega.PENDENTE, "FIAP TRANSPORTADORA" 
		));

		// Act & Assert
		given().filter(new AllureRestAssured())
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/api/entrega")
			.then().statusCode(HttpStatus.OK.value())
			.body(matchesJsonSchemaInClasspath("./schemas/EntregaSchemaArray.json"))
			.body("size()", equalTo(2));
	}

	@Test
	void devePermitirFinalizarEntrega() {
		// Arrange
		EntregaEntity entity = entregaRepository.save(new EntregaEntity(
			1, 999, 123, "Rua boa saguairu, 33, sao paulo", "02488-789", 
			LocalDateTime.now(), LocalDateTime.now().plusDays(5), null, StatusEntrega.PENDENTE, "FIAP TRANSPORTADORA" 
		));

		// Act & Assert
		given().filter(new AllureRestAssured())
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().put("/api/entrega/finalizar/{id}", entity.getId())
			.then().statusCode(HttpStatus.OK.value());
	}

	@Test
	void devePermitirObterEntrega() {
		// Arrange
		entregaRepository.save(new EntregaEntity(
			1, 999, 123, "Rua boa saguairu, 33, sao paulo", "02488-789", 
			LocalDateTime.now(), LocalDateTime.now().plusDays(5), null, StatusEntrega.PENDENTE, "FIAP TRANSPORTADORA" 
		));

		// Act & Assert
		given().filter(new AllureRestAssured())
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/api/entrega/1")
			.then().statusCode(HttpStatus.OK.value())
			.body(matchesJsonSchemaInClasspath("./schemas/EntregaSchema.json"));
	}

	@Test
	void devePermitirAtualizarEntrega() {
		// Arrange
		EntregaEntity entity = entregaRepository.save(new EntregaEntity(
			null, 999, 123, "Rua boa saguairu, 33, sao paulo", "02488-789", 
			LocalDateTime.now(), LocalDateTime.now().plusDays(5), null, StatusEntrega.PENDENTE, "FIAP TRANSPORTADORA" 
		));

		entity.setClienteid(2);
		
		EntregaRequestDTO request = new EntregaRequestDTO(entity.getPedidoid(), entity.getClienteid());

		// Act & Assert
		given().filter(new AllureRestAssured())
			.contentType(MediaType.APPLICATION_JSON_VALUE).body(request)
			.when().put("/api/entrega/{id}", entity.getId())
			.then().statusCode(HttpStatus.OK.value())
			.body(matchesJsonSchemaInClasspath("./schemas/EntregaSchema.json"));
	}

	@Test
	void devePermitirExcluirEntrega() {
		// Arrange
		EntregaEntity entity = entregaRepository.save(new EntregaEntity(
			null, 999, 123, "Rua boa saguairu, 33, sao paulo", "02488-789", 
			LocalDateTime.now(), LocalDateTime.now().plusDays(5), null, StatusEntrega.PENDENTE, "FIAP TRANSPORTADORA" 
		));

		// Act & Assert
		given().filter(new AllureRestAssured())
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().delete("/api/entrega/{id}", entity.getId())
			.then().statusCode(HttpStatus.OK.value());
	}

	@Test
	void devePermitirAgruparEntregaPendentePorCEP() {
		// Arrange
		entregaRepository.save(new EntregaEntity(
			1, 999, 123, "Rua boa saguairu, 33, sao paulo", "02488-789", 
			LocalDateTime.now(), LocalDateTime.now().plusDays(5), null, StatusEntrega.PENDENTE, "FIAP TRANSPORTADORA" 
		));

		entregaRepository.save(new EntregaEntity(
			2, 777, 222, "Rua XYZ, 33, sao paulo", "02433-789", 
			LocalDateTime.now(), LocalDateTime.now().plusDays(5), null, StatusEntrega.PENDENTE, "FIAP TRANSPORTADORA" 
		));

		entregaRepository.save(new EntregaEntity(
			3, 888, 222, "Rua ABC, 90, sao paulo", "12345-789", 
			LocalDateTime.now(), LocalDateTime.now().plusDays(5), null, StatusEntrega.PENDENTE, "FIAP TRANSPORTADORA" 
		));

		// Act & Assert
		given().filter(new AllureRestAssured())
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/api/entrega/agrupar/024")
			.then().statusCode(HttpStatus.OK.value())
			.body(matchesJsonSchemaInClasspath("./schemas/EntregaSchemaArray.json"))
			.body("size()", equalTo(2));
	}

	private EntregaRequestDTO gerarUmaEntregaRequestDTO() {
		return new EntregaRequestDTO(9999, 1);
	}
}
