package br.com.fiap.entrega.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.entrega.mapper.RastreamentoMapper;
import br.com.fiap.entrega.model.Rastreamento;
import br.com.fiap.entrega.model.entity.RastreamentoEntity;
import br.com.fiap.entrega.repository.RastreamentoRepository;
import br.com.fiap.entrega.service.RastreamentoService;

@Service
public class RastreamentoServiceImpl implements RastreamentoService {

   private final RastreamentoMapper rastreamentoMapper;
    private final RastreamentoRepository rastreamentoRepository;

    public RastreamentoServiceImpl(RastreamentoMapper entregaMapper, RastreamentoRepository entregaRepository) {
        this.rastreamentoMapper = entregaMapper;
        this.rastreamentoRepository = entregaRepository;
    }

    // Método para criar uma nova linha representando o rastreamento
    @Override
	@Transactional
    public Rastreamento criarRastreamento(Rastreamento rastreamento) {

        RastreamentoEntity entity = new RastreamentoEntity();
        entity.setPedidoid(rastreamento.getPedidoid());
        entity.setLatitude(rastreamento.getLatitude());
        entity.setLongitude(rastreamento.getLongitude());
        entity.setDataHora(LocalDateTime.now());

        RastreamentoEntity rastreamentoSalvo = rastreamentoRepository.save(entity);

        return rastreamentoMapper.converterRastreamentoEntityParaRastreamento(rastreamentoSalvo);
    }

    // Método para listar o rastreamento pelo id do Pedido
    public List<Rastreamento> listarRastreamentoPorPedidoId(Integer pedidoId) {
        List<RastreamentoEntity> rastreamentoEntityList = rastreamentoRepository.findByPedidoid(pedidoId);
        return rastreamentoEntityList.stream().map(rastreamentoMapper::converterRastreamentoEntityParaRastreamento).toList();
    }
}
