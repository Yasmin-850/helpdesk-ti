package br.com.helpdesk.helpdesk_ti.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.helpdesk.helpdesk_ti.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);
    long countByPerfil(String perfil);
}
