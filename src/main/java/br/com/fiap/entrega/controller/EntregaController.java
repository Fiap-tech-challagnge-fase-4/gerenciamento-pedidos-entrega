package br.com.fiap.entrega.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import br.com.fiap.entrega.mapper.EntregaMapper;
import br.com.fiap.entrega.model.Entrega;
import br.com.fiap.entrega.model.dto.EntregaResponseDTO;
import br.com.fiap.entrega.model.dto.EntregaRequestDTO;
import br.com.fiap.entrega.service.EntregaService;

import java.util.List;

@RestController
@RequestMapping("/api/entregas")
public class EntregaController {

    private final EntregaMapper entregaMapper;
	private final EntregaService entregaService;

    public EntregaController(EntregaService entregaService, EntregaMapper entregaMapper) {
        this.entregaService = entregaService;
        this.entregaMapper = entregaMapper;
    }

    @GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<EntregaResponseDTO> listarEntregas() {
		List<Entrega> entregas = entregaService.listarEntregas();
		return entregas.stream().map(entregaMapper::converterEntregaParaResponseDTO).toList();
	}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntregaResponseDTO criarEntrega(@RequestBody EntregaRequestDTO entregaRequestDTO) {
		Entrega entrega = entregaMapper.converterRequestDTOParaEntrega(entregaRequestDTO);
		Entrega entregaCriado = entregaService.criarEntrega(entrega);
		return entregaMapper.converterEntregaParaResponseDTO(entregaCriado);
	}

    @PutMapping("/finalizar/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void finalizarEntrega(@PathVariable Integer id) {
        entregaService.finalizarEntrega(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntregaResponseDTO  obterEntrega(@PathVariable Integer id) {
        Entrega entrega = entregaService.obterEntrega(id);
		return entregaMapper.converterEntregaParaResponseDTO(entrega);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntregaResponseDTO  atualizarEntrega(@PathVariable Integer id, @RequestBody EntregaRequestDTO entregaRequest) {
        Entrega entregaModificado = entregaMapper.converterRequestDTOParaEntrega(entregaRequest);
		Entrega entregaAtualizado = entregaService.atualizarEntrega(id, entregaModificado);
		return entregaMapper.converterEntregaParaResponseDTO(entregaAtualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void excluirEntrega(@PathVariable Integer id) {
        entregaService.excluirEntrega(id);
    }

    @GetMapping("/agrupar/{cep}")
    @ResponseStatus(HttpStatus.OK)
    public List<EntregaResponseDTO> agruparEntregaPendentePorCEP(@PathVariable String cep) {
        List<Entrega> entregas = entregaService.agruparEntregaPendentePorCEP(cep);
		return entregas.stream().map(entregaMapper::converterEntregaParaResponseDTO).toList();
    }
}