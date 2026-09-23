package br.com.helpdesk.helpdesk_ti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.helpdesk.helpdesk_ti.model.Chamado;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

}