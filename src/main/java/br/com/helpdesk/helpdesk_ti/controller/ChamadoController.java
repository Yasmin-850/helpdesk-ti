package br.com.helpdesk.helpdesk_ti.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.helpdesk.helpdesk_ti.model.Chamado;
import br.com.helpdesk.helpdesk_ti.service.ChamadoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/chamados")
public class ChamadoController {

    private final ChamadoService chamadoService;

    public ChamadoController(ChamadoService chamadoService) {
        this.chamadoService = chamadoService;
    }

    @GetMapping
    public List<Chamado> listarTodos() {
        return chamadoService.listarTodos();
    }

    @PostMapping
    public Chamado criar(@Valid @RequestBody Chamado chamado) {
        return chamadoService.salvar(chamado);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        chamadoService.excluir(id);
    }

    @PutMapping("/{id}")
    public Chamado atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Chamado chamado) {

        chamado.setId(id);
        return chamadoService.salvar(chamado);
    }
}