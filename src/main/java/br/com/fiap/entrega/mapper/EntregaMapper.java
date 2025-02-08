package br.com.fiap.entrega.mapper;

import org.springframework.stereotype.Component;

import br.com.fiap.entrega.model.Entrega;
import br.com.fiap.entrega.model.dto.EntregaRequestDTO;
import br.com.fiap.entrega.model.dto.EntregaResponseDTO;
import br.com.fiap.entrega.model.entity.EntregaEntity;

@Component
public class EntregaMapper {

     public Entrega converterRequestDTOParaEntrega(EntregaRequestDTO dto) {
        return new Entrega(
            dto.pedidoid(),
            dto.clienteid()
        );
    }

    public EntregaResponseDTO converterEntregaParaResponseDTO(Entrega entrega) {
        return new EntregaResponseDTO(
            entrega.getId(),
            entrega.getPedidoid(),
            entrega.getClienteid(),
            entrega.getDadosentrega(),
            entrega.getCep(),
            entrega.getDataenvio(),
            entrega.getDataprevistaentrega(),
            entrega.getDataentrega(),
            entrega.getStatusentrega(),
            entrega.getEntregador()
        );
    }

    public Entrega converterEntregaEntityParaEntrega(EntregaEntity entity) {
        return new Entrega(
            entity.getId(),
            entity.getPedidoid(),
            entity.getClienteid(),
            entity.getDadosentrega(),
            entity.getCep(),
            entity.getDataenvio(),
            entity.getDataprevistaentrega(),
            entity.getDataentrega(),
            entity.getStatusentrega(),
            entity.getEntregador()
        );
    }

    public EntregaEntity converterEntregaParaEntregaEntity(Entrega entrega) {
        
        EntregaEntity entity = new EntregaEntity();
        entity.setPedidoid(entrega.getPedidoid());
        entity.setClienteid(entrega.getClienteid());
        entity.setDataprevistaentrega(entrega.getDataprevistaentrega());
        entity.setStatusentrega(entrega.getStatusentrega());
        entity.setEntregador(entrega.getEntregador());
        entity.setDadosentrega(entrega.getDadosentrega());
        entity.setCep(entrega.getCep());

        return entity;
    }
}
