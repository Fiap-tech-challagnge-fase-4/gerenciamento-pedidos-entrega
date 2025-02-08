package br.com.fiap.entrega.model.dto;

public record RastreamentoRequestDTO (
    Integer pedidoid,
    String latitude,
    String longitude
){}