package br.com.fiap.entrega.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.entrega.enums.StatusEntrega;
import br.com.fiap.entrega.model.entity.EntregaEntity;

public interface EntregaRepository extends JpaRepository<EntregaEntity, Integer> {
    List<EntregaEntity> findByCepStartingWithAndStatusentrega(String cepPrefix, StatusEntrega statusEntrega);
}