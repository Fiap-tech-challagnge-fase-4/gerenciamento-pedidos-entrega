package br.com.fiap.entrega.service;

import java.util.List;

import org.springframework.stereotype.Service;
import br.com.fiap.entrega.model.Entrega;

@Service
public interface EntregaService {

    public List<Entrega> listarEntregas();

    public Entrega criarEntrega(Entrega entrega);

    public void finalizarEntrega(Integer entregaid);
    
    public Entrega obterEntrega(Integer id);

    public Entrega atualizarEntrega(Integer id, Entrega entrega);

    public void excluirEntrega(Integer id);

    public List<Entrega> agruparEntregaPendentePorCEP(String cep);
}
