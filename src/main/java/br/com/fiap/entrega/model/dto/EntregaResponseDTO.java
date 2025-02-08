package br.com.fiap.entrega.model.dto;

import java.time.LocalDateTime;

import br.com.fiap.entrega.enums.StatusEntrega;

public record EntregaResponseDTO (
    Integer id,
    Integer pedidoid,
    Integer clienteid,
    String dadosentrega,
    String cep,
    LocalDateTime dataenvio,
    LocalDateTime dataprevistaentrega,
    LocalDateTime dataentrega,
    StatusEntrega statusentrega,
    String entregador
){}