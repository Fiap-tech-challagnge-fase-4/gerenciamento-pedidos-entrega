package br.com.fiap.entrega.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.fiap.entrega.model.Entrega;
import br.com.fiap.entrega.repository.EntregaRepository;

@Service
public class EntregaService {

    @Autowired
    private EntregaRepository entregaRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${pedido.url}")
    private String pedidoURL;

    // Método para listar todos os entregas
    public List<Entrega> listarEntrega() {
        return entregaRepository.findAll();
    }

    // Método para criar um novo entrega
    public Entrega criarEntrega(Entrega entrega) {
        return entregaRepository.save(entrega);
    }

    // Método para criar um novo entrega
    public Entrega finalizarEntrega(Integer entregaid) {

        var entrega = obterEntrega(entregaid);
        entrega.setDataentrega(LocalDateTime.now());
        entrega.setStatusentrega("ENTREGUE");

        //Finalizar o pedido
        restTemplate.put(pedidoURL + "finalizar/" + entregaid, null);

        return entregaRepository.save(entrega);
    }
    
    // Método para obter um entrega pelo ID
    public Entrega obterEntrega(Integer id) {
        Optional<Entrega> entrega = entregaRepository.findById(id);
        return entrega.orElse(null);
    }

    // Método para atualizar um entrega existente
    public Entrega atualizarEntrega(Integer id, Entrega entrega) {
        if (entregaRepository.existsById(id)) {
            entrega.setId(id);
            return entregaRepository.save(entrega);
        }
        return null;
    }

    // Método para excluir um entrega pelo ID
    public void excluirEntrega(Integer id) {
        entregaRepository.deleteById(id);
    }
}
