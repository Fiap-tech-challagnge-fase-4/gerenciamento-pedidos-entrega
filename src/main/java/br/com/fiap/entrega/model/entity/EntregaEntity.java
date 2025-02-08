package br.com.fiap.entrega.model.entity;

import java.time.LocalDateTime;

import br.com.fiap.entrega.enums.StatusEntrega;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class EntregaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer pedidoid;
    private Integer clienteid;
    private String dadosentrega;
    private String cep;
    private LocalDateTime dataenvio;
    private LocalDateTime dataprevistaentrega;
    private LocalDateTime dataentrega;
    @Enumerated(EnumType.STRING)
    private StatusEntrega statusentrega;
    private String entregador;

}
