package br.com.helpdesk.helpdesk_ti.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.helpdesk.helpdesk_ti.model.Chamado;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

    List<Chamado> findByStatusIgnoreCase(String status);

    List<Chamado> findBySetorIgnoreCase(String setor);

    List<Chamado> findByPrioridadeIgnoreCase(String prioridade);

    List<Chamado> findBySolicitanteIgnoreCase(String solicitante);

    List<Chamado> findByCriadorEmailIgnoreCase(String criadorEmail);

}