package br.com.fiap.entrega.model;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class Rastreamento {
    private Integer id;
    private Integer pedidoid;
    private String latitude;
    private String longitude;
    private LocalDateTime dataHora;
    
    public Rastreamento(Integer id, Integer pedidoid, String latitude, String longitude, LocalDateTime dataHora) {
        this.id = id;
        this.pedidoid = pedidoid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dataHora = dataHora;
    }

    public Rastreamento(Integer pedidoid, String latitude, String longitude) {
        this.pedidoid = pedidoid;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
