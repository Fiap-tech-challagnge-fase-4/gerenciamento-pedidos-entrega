package br.com.fiap.entrega.model.dto;

import java.time.LocalDateTime;

public record RastreamentoResponseDTO(
    Integer id,
    Integer pedidoid,
    String latitude,
    String longitude,
    LocalDateTime dataHora
){}
