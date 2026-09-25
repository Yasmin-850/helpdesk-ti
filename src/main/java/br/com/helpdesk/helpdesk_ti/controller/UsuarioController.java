package br.com.helpdesk.helpdesk_ti.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import br.com.helpdesk.helpdesk_ti.dto.*;
import br.com.helpdesk.helpdesk_ti.service.UsuarioService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService service;
    public UsuarioController(UsuarioService service){this.service=service;}

    private UsuarioSessao sessao(HttpSession session){
        UsuarioSessao u=(UsuarioSessao)session.getAttribute("usuario");
        if(u==null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Não autenticado");
        return u;
    }
    private void exigirAdmin(HttpSession session){
        if(!"ADMIN".equals(sessao(session).perfil())) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Acesso exclusivo do administrador");
    }
    @GetMapping public List<UsuarioResumo> listar(HttpSession s){ exigirAdmin(s); return service.listar(); }
    @GetMapping("/me") public UsuarioResumo meuPerfil(HttpSession s){ UsuarioSessao u=sessao(s); return service.buscarResumo(u.id()); }
    @PutMapping("/me") public UsuarioResumo editarMeuPerfil(@RequestBody EditarUsuarioRequest r,HttpSession s){
        UsuarioSessao u=sessao(s);
        UsuarioResumo atualizado=service.editarMeuPerfil(u.id(),r.nome(),r.email(),r.setor());
        s.setAttribute("usuario", new UsuarioSessao(atualizado.id(),atualizado.nome(),atualizado.email(),atualizado.perfil(),atualizado.setor()));
        return atualizado;
    }
    @PostMapping public UsuarioResumo criar(@RequestBody NovoUsuarioRequest r,HttpSession s){ exigirAdmin(s); return service.criar(r.nome(),r.email(),r.senha(),r.perfil(),r.setor()); }
    @GetMapping("/{id}") public UsuarioResumo buscar(@PathVariable Long id,HttpSession s){ exigirAdmin(s); return service.buscarResumo(id); }
    @PutMapping("/{id}") public UsuarioResumo editar(@PathVariable Long id,@RequestBody EditarUsuarioRequest r,HttpSession s){ UsuarioSessao u=sessao(s); exigirAdmin(s); return service.editar(id,r.nome(),r.email(),r.perfil(),r.setor(),u.id()); }
    @DeleteMapping("/{id}") public void excluir(@PathVariable Long id,HttpSession s){ UsuarioSessao u=sessao(s); exigirAdmin(s); service.excluir(id,u.id()); }
        @PutMapping("/minha-senha") public void alterarSenha(@RequestBody AlterarSenhaRequest r,HttpSession s){ UsuarioSessao u=sessao(s); service.alterarSenha(u.id(),r.senhaAtual(),r.novaSenha()); }
}
