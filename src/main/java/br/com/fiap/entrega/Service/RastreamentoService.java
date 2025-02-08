package br.com.fiap.entrega.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.fiap.entrega.model.Rastreamento;

@Service
public interface RastreamentoService {

    public Rastreamento criarRastreamento(Rastreamento rastreamento);

    public List<Rastreamento> listarRastreamentoPorPedidoId(Integer pedidoId);
    
}
