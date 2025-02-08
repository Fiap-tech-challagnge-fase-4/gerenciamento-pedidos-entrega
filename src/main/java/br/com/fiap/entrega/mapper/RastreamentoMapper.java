package br.com.fiap.entrega.mapper;

import org.springframework.stereotype.Component;

import br.com.fiap.entrega.model.Rastreamento;
import br.com.fiap.entrega.model.dto.RastreamentoRequestDTO;
import br.com.fiap.entrega.model.dto.RastreamentoResponseDTO;
import br.com.fiap.entrega.model.entity.RastreamentoEntity;

@Component
public class RastreamentoMapper {
    
    public Rastreamento converterRequestDTOParaRastreamento(RastreamentoRequestDTO dto) {
        return new Rastreamento(
            dto.pedidoid(),
            dto.latitude(),
            dto.longitude()
        );
    }

    public RastreamentoResponseDTO converterRastreamentoParaResponseDTO(Rastreamento rastreamento) {
        return new RastreamentoResponseDTO(
            rastreamento.getId(),
            rastreamento.getPedidoid(),
            rastreamento.getLatitude(),
            rastreamento.getLongitude(),
            rastreamento.getDataHora()
        );
    }

    public Rastreamento converterRastreamentoEntityParaRastreamento(RastreamentoEntity entity) {
        return new Rastreamento(
            entity.getId(),
            entity.getPedidoid(),
            entity.getLatitude(),
            entity.getLongitude(),
            entity.getDataHora()
        );
    }
}
