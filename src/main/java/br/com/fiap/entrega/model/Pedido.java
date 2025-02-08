package br.com.fiap.entrega.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.com.fiap.entrega.enums.StatusPedido;
import lombok.Getter;

@Getter
public class Pedido implements Serializable{
    private Integer id;
    private Integer clienteid;
    private List<ItemPedido> itens;
    private BigDecimal valortotal;
    private StatusPedido status;
    private LocalDateTime datacriacao;
    private LocalDateTime dataconclusao;
}



