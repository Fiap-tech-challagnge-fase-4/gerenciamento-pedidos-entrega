package br.com.fiap.entrega.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.entrega.mapper.RastreamentoMapper;
import br.com.fiap.entrega.model.Rastreamento;
import br.com.fiap.entrega.model.dto.RastreamentoRequestDTO;
import br.com.fiap.entrega.model.dto.RastreamentoResponseDTO;
import br.com.fiap.entrega.service.RastreamentoService;

@RestController
@RequestMapping("/api/rastreamento")
public class RastreamentoController {

    private final RastreamentoMapper rastreamentoMapper;
	private final RastreamentoService rastreamentoService;

    public RastreamentoController(RastreamentoService rastreamentoService, RastreamentoMapper rastreamentoMapper) {
        this.rastreamentoService = rastreamentoService;
        this.rastreamentoMapper = rastreamentoMapper;
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RastreamentoResponseDTO criarRastreamento(@RequestBody RastreamentoRequestDTO rastreamentoRequestDTO) {
		Rastreamento rastreamento = rastreamentoMapper.converterRequestDTOParaRastreamento(rastreamentoRequestDTO);
		Rastreamento rastreamentoCriado = rastreamentoService.criarRastreamento(rastreamento);
		return rastreamentoMapper.converterRastreamentoParaResponseDTO(rastreamentoCriado);
	}

    @GetMapping("/{pedidoId}")
    @ResponseStatus(HttpStatus.OK)
    public List<RastreamentoResponseDTO> listarRastreamentoPorPedidoId(@PathVariable Integer pedidoId) {
        List<Rastreamento> rastreamentos = rastreamentoService.listarRastreamentoPorPedidoId(pedidoId);
		return rastreamentos.stream().map(rastreamentoMapper::converterRastreamentoParaResponseDTO).toList();
    }
}