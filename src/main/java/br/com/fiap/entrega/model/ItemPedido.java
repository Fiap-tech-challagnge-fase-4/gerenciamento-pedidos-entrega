package br.com.fiap.entrega.model;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;

@Getter
public class ItemPedido implements Serializable{
    private Integer id;
    private Pedido pedido;
    private Integer produtoid;
    private Integer quantidade;
    private BigDecimal precounitario;
}



