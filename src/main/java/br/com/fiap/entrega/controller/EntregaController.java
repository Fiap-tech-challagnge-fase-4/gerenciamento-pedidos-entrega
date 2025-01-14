package br.com.fiap.entrega.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.fiap.entrega.Service.EntregaService;
import br.com.fiap.entrega.model.Entrega;

import java.util.List;

@RestController
@RequestMapping("/api/entrega")
public class EntregaController {

    @Autowired
    private EntregaService entregaService;

    @GetMapping
    public List<Entrega> listarEntrega() {
        return entregaService.listarEntrega();
    }

    @PostMapping
    public Entrega criarEntrega(@RequestBody Entrega entrega) {
        return entregaService.criarEntrega(entrega);
    }

    @PutMapping("/finalizar/{id}")
    public Entrega finalizarEntrega(@PathVariable Integer id) {
        return entregaService.finalizarEntrega(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Entrega> obterEntrega(@PathVariable Integer id) {
        Entrega entrega = entregaService.obterEntrega(id);
        return entrega != null ? ResponseEntity.ok(entrega) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Entrega> atualizarEntrega(@PathVariable Integer id, @RequestBody Entrega entrega) {
        Entrega entregaAtualizado = entregaService.atualizarEntrega(id, entrega);
        return entregaAtualizado != null ? ResponseEntity.ok(entregaAtualizado) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirEntrega(@PathVariable Integer id) {
        entregaService.excluirEntrega(id);
        return ResponseEntity.noContent().build();
    }
}