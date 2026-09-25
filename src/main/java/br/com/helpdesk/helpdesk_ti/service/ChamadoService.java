package br.com.helpdesk.helpdesk_ti.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import br.com.helpdesk.helpdesk_ti.model.Chamado;
import br.com.helpdesk.helpdesk_ti.repository.ChamadoRepository;

@Service
public class ChamadoService {

    private final ChamadoRepository chamadoRepository;

    public ChamadoService(ChamadoRepository chamadoRepository) {
        this.chamadoRepository = chamadoRepository;
    }

    // LISTAR TODOS
    public List<Chamado> listarTodos() {
        return chamadoRepository.findAll();
    }

    // BUSCAR POR ID
    public Chamado buscarPorId(Long id) {
        return chamadoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Chamado não encontrado"
                ));
    }

    // SALVAR / CRIAR
    public Chamado salvar(Chamado chamado) {
        return chamadoRepository.save(chamado);
    }

    // EXCLUIR
    public void excluir(Long id) {

        Chamado chamado = buscarPorId(id);

        chamadoRepository.delete(chamado);
    }

    // BUSCAR POR STATUS
    public List<Chamado> buscarPorStatus(String status) {
        return chamadoRepository.findByStatusIgnoreCase(status);
    }

    // BUSCAR POR PRIORIDADE
    public List<Chamado> buscarPorPrioridade(String prioridade) {
        return chamadoRepository.findByPrioridadeIgnoreCase(prioridade);
    }

    // BUSCAR POR SETOR
    public List<Chamado> buscarPorSetor(String setor) {
        return chamadoRepository.findBySetorIgnoreCase(setor);
    }

    // BUSCAR POR SOLICITANTE
    public List<Chamado> buscarPorSolicitante(String solicitante) {
        return chamadoRepository.findBySolicitanteIgnoreCase(solicitante);
    }

    public List<Chamado> buscarPorCriadorEmail(String email) {
        return chamadoRepository.findByCriadorEmailIgnoreCase(email);
    }

    // ALTERAR STATUS
    public Chamado alterarStatus(Long id, String status) {

        Chamado chamado = buscarPorId(id);

        chamado.setStatus(status);

        return chamadoRepository.save(chamado);
    }
}