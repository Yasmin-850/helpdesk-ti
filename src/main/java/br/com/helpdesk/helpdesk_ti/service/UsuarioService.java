package br.com.helpdesk.helpdesk_ti.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import br.com.helpdesk.helpdesk_ti.dto.UsuarioResumo;
import br.com.helpdesk.helpdesk_ti.model.Usuario;
import br.com.helpdesk.helpdesk_ti.repository.UsuarioRepository;

@Service
public class UsuarioService {
    private final UsuarioRepository repository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public UsuarioService(UsuarioRepository repository){ this.repository=repository; }

    public Usuario autenticar(String email, String senha){
        if(email==null || senha==null) return null;
        return repository.findByEmailIgnoreCase(email.trim())
            .filter(u -> encoder.matches(senha, u.getSenha())).orElse(null);
    }

    public Usuario criarSeNaoExiste(String nome, String email, String senha, String perfil, String setor){
        return repository.findByEmailIgnoreCase(email).orElseGet(() ->
            repository.save(new Usuario(nome,email,encoder.encode(senha),perfil,setor)));
    }

    public List<UsuarioResumo> listar(){
        return repository.findAll().stream()
            .map(u -> new UsuarioResumo(u.getId(),u.getNome(),u.getEmail(),u.getPerfil(),u.getSetor())).toList();
    }

    public UsuarioResumo buscarResumo(Long id){
        Usuario u=repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Usuário não encontrado"));
        return resumo(u);
    }

    private UsuarioResumo resumo(Usuario u){
        return new UsuarioResumo(u.getId(),u.getNome(),u.getEmail(),u.getPerfil(),u.getSetor());
    }

    public UsuarioResumo editar(Long id,String nome,String email,String perfil,String setor,Long usuarioLogadoId){
        Usuario u=repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Usuário não encontrado"));
        if(nome==null || nome.isBlank() || email==null || email.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Preencha nome e e-mail");
        String p=perfil==null ? u.getPerfil() : perfil.toUpperCase();
        if(!p.equals("ADMIN") && !p.equals("SOLICITANTE"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Perfil inválido");
        repository.findByEmailIgnoreCase(email.trim()).ifPresent(outro -> {
            if(!outro.getId().equals(id)) throw new ResponseStatusException(HttpStatus.CONFLICT,"Já existe um usuário com este e-mail");
        });
        if("ADMIN".equals(u.getPerfil()) && !"ADMIN".equals(p) && repository.countByPerfil("ADMIN") <= 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Não é possível remover o perfil do último administrador");
        if(id.equals(usuarioLogadoId) && !"ADMIN".equals(p))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Você não pode remover seu próprio perfil de administrador enquanto está conectado");
        u.setNome(nome.trim());
        u.setEmail(email.trim().toLowerCase());
        u.setPerfil(p);
        u.setSetor(setor==null ? "" : setor.trim());
        return resumo(repository.save(u));
    }

    public UsuarioResumo criar(String nome,String email,String senha,String perfil,String setor){
        if(nome==null || nome.isBlank() || email==null || email.isBlank() || senha==null || senha.length()<6)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Preencha nome, e-mail e uma senha com pelo menos 6 caracteres");
        String p = perfil==null ? "SOLICITANTE" : perfil.toUpperCase();
        if(!p.equals("ADMIN") && !p.equals("SOLICITANTE"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Perfil inválido");
        if(repository.findByEmailIgnoreCase(email.trim()).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Já existe um usuário com este e-mail");
        Usuario u=repository.save(new Usuario(nome.trim(),email.trim().toLowerCase(),encoder.encode(senha),p,setor==null?"":setor.trim()));
        return resumo(u);
    }

    public void excluir(Long id, Long usuarioLogadoId){
        if(id == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Usuário inválido");
        if(id.equals(usuarioLogadoId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Você não pode excluir o usuário que está conectado");

        Usuario u=repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Usuário não encontrado"));

        if("ADMIN".equals(u.getPerfil()) && repository.countByPerfil("ADMIN") <= 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Não é possível excluir o último administrador do sistema");

        repository.delete(u);
    }

    public UsuarioResumo buscarPorEmail(String email){
        Usuario u=repository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Usuário não encontrado"));
        return resumo(u);
    }

    public UsuarioResumo editarMeuPerfil(Long id, String nome, String email, String setor){
        Usuario u=repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Usuário não encontrado"));
        if(nome==null || nome.isBlank() || email==null || email.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Preencha nome e e-mail");
        repository.findByEmailIgnoreCase(email.trim()).ifPresent(outro -> {
            if(!outro.getId().equals(id)) throw new ResponseStatusException(HttpStatus.CONFLICT,"Já existe um usuário com este e-mail");
        });
        u.setNome(nome.trim());
        u.setEmail(email.trim().toLowerCase());
        u.setSetor(setor==null ? "" : setor.trim());
        return resumo(repository.save(u));
    }

    public void alterarSenha(Long id,String atual,String nova){
        Usuario u=repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Usuário não encontrado"));
        if(atual==null || !encoder.matches(atual,u.getSenha()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Senha atual incorreta");
        if(nova==null || nova.length()<6)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"A nova senha deve ter pelo menos 6 caracteres");
        u.setSenha(encoder.encode(nova)); repository.save(u);
    }
}
