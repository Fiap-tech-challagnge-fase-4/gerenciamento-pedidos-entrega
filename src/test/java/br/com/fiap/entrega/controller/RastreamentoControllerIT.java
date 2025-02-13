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

import br.com.fiap.entrega.model.dto.RastreamentoRequestDTO;
import br.com.fiap.entrega.model.entity.RastreamentoEntity;
import br.com.fiap.entrega.repository.RastreamentoRepository;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;

@AutoConfigureTestDatabase
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RastreamentoControllerIT {
    
    @LocalServerPort
	private int port;

    @Autowired
    private RastreamentoRepository rastreamentoRepository;

	@BeforeEach
	public void setup() {
	    RestAssured.port = port;
	    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
	}

	@Test
	void devePermitirCriarRastreamento() {
		// Arrange
		RastreamentoRequestDTO request = gerarUmRastreamentoRequestDTO();
	
		// Act & Assert
		given().filter(new AllureRestAssured()).contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(request)
			.when().post("/api/rastreamentos")
			.then().statusCode(HttpStatus.CREATED.value())
			.body(matchesJsonSchemaInClasspath("./schemas/RastreamentoSchema.json"))
			.body("$", hasKey("pedidoid"))
			.body("$", hasKey("latitude"))
			.body("$", hasKey("longitude"))
			.body("$", hasKey("dataHora"))
			.body("pedidoid", greaterThan(0));
	}

	@Test
	void devePermitirlistarRastreamentoPorPedidoId() {
	
		rastreamentoRepository.save(new RastreamentoEntity(
			null, 999, "12345", "-678910",
			LocalDateTime.now()
		));

		rastreamentoRepository.save(new RastreamentoEntity(
			null, 999, "54321", "-109875",
			LocalDateTime.now()
		));

		// Act & Assert
		given().filter(new AllureRestAssured())
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/api/rastreamentos/999")
			.then().statusCode(HttpStatus.OK.value())
			.body(matchesJsonSchemaInClasspath("./schemas/RastreamentoSchemaArray.json"))
			.body("size()", equalTo(2));
	}

	private RastreamentoRequestDTO gerarUmRastreamentoRequestDTO() {
		return new RastreamentoRequestDTO(1, "12345", "-56789");
	}
}
