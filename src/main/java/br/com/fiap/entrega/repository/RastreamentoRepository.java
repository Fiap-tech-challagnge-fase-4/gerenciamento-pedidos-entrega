package br.com.fiap.entrega.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.entrega.model.entity.RastreamentoEntity;

public interface RastreamentoRepository extends JpaRepository<RastreamentoEntity, Integer> {
    List<RastreamentoEntity> findByPedidoid(Integer pedidoId);
}