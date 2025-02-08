package br.com.fiap.entrega.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;

import br.com.fiap.entrega.enums.StatusEntrega;
import br.com.fiap.entrega.mapper.EntregaMapper;
import br.com.fiap.entrega.model.Entrega;
import br.com.fiap.entrega.model.Rastreamento;
import br.com.fiap.entrega.model.entity.EntregaEntity;
import br.com.fiap.entrega.repository.EntregaRepository;
import br.com.fiap.entrega.service.EntregaService;
import br.com.fiap.entrega.service.RastreamentoService;
import br.com.fiap.entrega.utils.Tools;
import jakarta.persistence.EntityNotFoundException;

@Service
public class EntregaServiceImpl implements EntregaService {

    private final EntregaMapper entregaMapper;
    private final EntregaRepository entregaRepository;
    private Tools tool = new Tools();

    public EntregaServiceImpl(EntregaMapper entregaMapper, EntregaRepository entregaRepository) {
        this.entregaMapper = entregaMapper;
        this.entregaRepository = entregaRepository;
    }

    @Value("${pedido.url}")
    private String pedidoURL;

    @Value("${cliente.url}")
    private String clienteURL;

    @Value("${endereco.centrodistribuicao}")
    private String enderecoCD;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RastreamentoService rastreamentoService;

    // Metodo para retornar uma entidade pelo id
    private EntregaEntity obterEntregaEntityPorId(Integer id) {
        return entregaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entrega não encontrada."));
    }

    // Método para listar todos os entregas
    public List<Entrega> listarEntregas() {
        List<EntregaEntity> entregaEntityList = entregaRepository.findAll();
        return entregaEntityList.stream().map(entregaMapper::converterEntregaEntityParaEntrega).toList();
    }

    // Método para criar uma nova entrega
    @Override
    @Transactional
    public Entrega criarEntrega(Entrega entrega) {

        try {
            entrega.atribuirDadosComplementaresDoCliente(restTemplate, objectMapper, clienteURL);
            // Atribuir o rastreamento para a posição do centro de distribuição
            Pair<String, String> latitudeAndlongitude = tool.getCordenadasPeloEndereco(enderecoCD);
            rastreamentoService.criarRastreamento(new Rastreamento(entrega.getPedidoid(),
                    latitudeAndlongitude.getFirst(), latitudeAndlongitude.getSecond()));

        } catch (Exception e) {
            // TODO: Apenas logar o erro, não impactar a criação da entrega por conta de
            // rastreamento
        }

        EntregaEntity entity = entregaMapper.converterEntregaParaEntregaEntity(entrega);

        EntregaEntity entregaSalva = entregaRepository.save(entity);

        return entregaMapper.converterEntregaEntityParaEntrega(entregaSalva);
    }

    // Método para finalizar uma entrega
    @Override
    @Transactional
    public void finalizarEntrega(Integer entregaid) {

        EntregaEntity entregaEntity = obterEntregaEntityPorId(entregaid);
        Entrega entrega = entregaMapper.converterEntregaEntityParaEntrega(entregaEntity);

        if (!entrega.isPendente()) {
            throw new IllegalStateException("A entrega não pode ser mais modificada.");
        }

        // Atribui as propriedades que representam a finalização da entrega
        entregaEntity.setDataentrega(LocalDateTime.now());
        entregaEntity.setStatusentrega(StatusEntrega.ENTREGUE);

        try {
            // Finalizar o pedido
            restTemplate.put(pedidoURL + "finalizar/" + entrega.getPedidoid(), null);

            // Atribuir o rastreamento para a posição do cliente
            Pair<String, String> latitudeAndlongitude = tool.getCordenadasPeloEndereco(entrega.getDadosentrega());
            rastreamentoService.criarRastreamento(new Rastreamento(entrega.getPedidoid(),
                    latitudeAndlongitude.getFirst(), latitudeAndlongitude.getSecond()));

        } catch (Exception e) {
            // TODO: Apenas logar o erro, não impactar a criação da entrega por conta de
            // rastreamento ou finalização de pedido
        }

        entregaRepository.save(entregaEntity);
    }

    // Método para excluir um entrega pelo ID de forma logica
    @Override
    @Transactional
    public void excluirEntrega(Integer entregaid) {

        EntregaEntity entregaEntity = obterEntregaEntityPorId(entregaid);
        Entrega entrega = entregaMapper.converterEntregaEntityParaEntrega(entregaEntity);

        if (!entrega.isPendente()) {
            throw new IllegalStateException("A entrega não pode ser mais modificada.");
        }

        // Atribui a propriedade que representam remoção logica da entrega
        entregaEntity.setStatusentrega(StatusEntrega.CANCELADA);

        entregaRepository.save(entregaEntity);
    }

    // Método para obter um entrega pelo ID
    @Override
    public Entrega obterEntrega(Integer id) {
        EntregaEntity entregaEntity = obterEntregaEntityPorId(id);

        return entregaMapper.converterEntregaEntityParaEntrega(entregaEntity);
    }

    // Método para atualizar um entrega existente
    @Override
    @Transactional
    public Entrega atualizarEntrega(Integer id, Entrega entregaRequest) {
        EntregaEntity entregaEntity = obterEntregaEntityPorId(id);

        try {
            entregaRequest.atribuirDadosComplementaresDoCliente(restTemplate, objectMapper, clienteURL);
        } catch (Exception e) {
            // TODO: Apenas logar o erro, não impactar a criação da entrega por conta de
            // adição de dados complementares
        }

        Entrega entrega = entregaMapper.converterEntregaEntityParaEntrega(entregaEntity);

        if (!entrega.isPendente()) {
            throw new IllegalStateException("A entrega não pode ser mais atualizada.");
        }

        entregaEntity.setPedidoid(entregaRequest.getPedidoid());
        entregaEntity.setClienteid(entregaRequest.getClienteid());
        entregaEntity.setDadosentrega(entregaRequest.getDadosentrega());
        entregaEntity.setCep(entregaRequest.getCep());
        entregaEntity.setDataenvio(entregaRequest.getDataenvio());
        entregaEntity.setDataprevistaentrega(entregaRequest.getDataprevistaentrega());
        entregaEntity.setDataentrega(entregaRequest.getDataentrega());
        entregaEntity.setStatusentrega(entregaRequest.getStatusentrega());
        entregaEntity.setEntregador(entregaRequest.getEntregador());

        EntregaEntity entregaEntityAtualizado = entregaRepository.save(entregaEntity);

        return entregaMapper.converterEntregaEntityParaEntrega(entregaEntityAtualizado);
    }

    // Método para agrupar entregas pelo prefixo do CEP
    public List<Entrega> agruparEntregaPendentePorCEP(String cep) {
        List<EntregaEntity> entregaEntityList = entregaRepository.findByCepStartingWithAndStatusentrega(cep,
                StatusEntrega.PENDENTE);
        return entregaEntityList.stream().map(entregaMapper::converterEntregaEntityParaEntrega).toList();
    }
}
