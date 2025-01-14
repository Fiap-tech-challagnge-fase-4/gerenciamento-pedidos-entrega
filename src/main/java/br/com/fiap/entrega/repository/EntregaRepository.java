package br.com.fiap.entrega.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.entrega.model.Entrega;

public interface EntregaRepository extends JpaRepository<Entrega, Integer> {
}

