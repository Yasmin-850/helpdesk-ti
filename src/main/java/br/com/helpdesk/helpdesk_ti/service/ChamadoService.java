package br.com.helpdesk.helpdesk_ti.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.helpdesk.helpdesk_ti.model.Chamado;
import br.com.helpdesk.helpdesk_ti.repository.ChamadoRepository;

@Service
public class ChamadoService {

    private final ChamadoRepository chamadoRepository;

    public ChamadoService(ChamadoRepository chamadoRepository) {
        this.chamadoRepository = chamadoRepository;
    }

    public List<Chamado> listarTodos() {
        return chamadoRepository.findAll();
    }

    public Chamado buscarPorId(Long id) {
        return chamadoRepository.findById(id).orElse(null);
    }

    public Chamado salvar(Chamado chamado) {
        return chamadoRepository.save(chamado);
    }

    public void excluir(Long id) {
        chamadoRepository.deleteById(id);
    }

    public List<Chamado> buscarPorStatus(String status) {
        return chamadoRepository.findByStatusIgnoreCase(status);
    }

    public List<Chamado> buscarPorPrioridade(String prioridade) {
        return chamadoRepository.findByPrioridadeIgnoreCase(prioridade);
    }
}