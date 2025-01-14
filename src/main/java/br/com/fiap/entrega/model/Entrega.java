package br.com.fiap.entrega.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Entrega {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer pedidoid;
    private Integer clienteid;
    private String dadosentrega;
    private LocalDateTime dataenvio;
    private LocalDateTime dataprevistaentrega;
    private LocalDateTime dataentrega;
    private String statusentrega;
    private String transportadora;
    private String rastreamentocodigo;
}
