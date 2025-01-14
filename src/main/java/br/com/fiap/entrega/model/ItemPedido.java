package br.com.fiap.entrega.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

@Data
public class ItemPedido implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne 
    @JoinColumn(name = "pedidoid") 
    @JsonBackReference
    private Pedido pedido;
    private Integer produtoid;
    private Integer quantidade;
    private BigDecimal precounitario;
}

