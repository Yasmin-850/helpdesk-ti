package br.com.helpdesk.helpdesk_ti.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import br.com.helpdesk.helpdesk_ti.dto.UsuarioSessao;
import br.com.helpdesk.helpdesk_ti.model.Chamado;
import br.com.helpdesk.helpdesk_ti.service.ChamadoService;
import br.com.helpdesk.helpdesk_ti.service.UsuarioService;
import br.com.helpdesk.helpdesk_ti.dto.UsuarioResumo;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/chamados")
public class ChamadoController {
    private final ChamadoService chamadoService;
    private final UsuarioService usuarioService;
    public ChamadoController(ChamadoService chamadoService, UsuarioService usuarioService){this.chamadoService=chamadoService; this.usuarioService=usuarioService;}
    private UsuarioSessao usuario(HttpSession s){return (UsuarioSessao)s.getAttribute("usuario");}
    private boolean admin(UsuarioSessao u){return "ADMIN".equals(u.perfil());}
    private Chamado autorizado(Long id, UsuarioSessao u){
        Chamado c=chamadoService.buscarPorId(id);
        if(!admin(u) && (c.getCriadorEmail()==null || !c.getCriadorEmail().equalsIgnoreCase(u.email())))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Sem permissão para este chamado");
        return c;
    }
    @GetMapping public List<Chamado> listarTodos(HttpSession s){
        UsuarioSessao u=usuario(s); return admin(u)?chamadoService.listarTodos():chamadoService.buscarPorCriadorEmail(u.email());
    }
    @GetMapping("/{id}") public Chamado buscarPorId(@PathVariable Long id,HttpSession s){return autorizado(id,usuario(s));}
    @GetMapping("/status/{status}") public List<Chamado> buscarPorStatus(@PathVariable String status,HttpSession s){
        return listarTodos(s).stream().filter(c->status.equalsIgnoreCase(c.getStatus())).toList();
    }
    @GetMapping("/prioridade/{prioridade}") public List<Chamado> buscarPorPrioridade(@PathVariable String prioridade,HttpSession s){
        return listarTodos(s).stream().filter(c->prioridade.equalsIgnoreCase(c.getPrioridade())).toList();
    }
    @GetMapping("/setor/{setor}") public List<Chamado> buscarPorSetor(@PathVariable String setor,HttpSession s){
        return listarTodos(s).stream().filter(c->setor.equalsIgnoreCase(c.getSetor())).toList();
    }
    @GetMapping("/solicitante/{solicitante}") public List<Chamado> buscarPorSolicitante(@PathVariable String solicitante,HttpSession s){
        return listarTodos(s).stream().filter(c->solicitante.equalsIgnoreCase(c.getSolicitante())).toList();
    }
    @PostMapping public Chamado criar(@Valid @RequestBody Chamado chamado,HttpSession s){
        UsuarioSessao u=usuario(s); chamado.setId(null);
        if(!admin(u)){
            chamado.setCriadorEmail(u.email()); chamado.setSolicitante(u.nome()); if(u.setor()!=null&&!u.setor().isBlank()) chamado.setSetor(u.setor()); chamado.setStatus("ABERTO");
        } else {
            if(chamado.getCriadorEmail()==null || chamado.getCriadorEmail().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Selecione um solicitante cadastrado");
            UsuarioResumo escolhido=usuarioService.buscarPorEmail(chamado.getCriadorEmail());
            chamado.setSolicitante(escolhido.nome()); chamado.setSetor(escolhido.setor()==null?"":escolhido.setor()); chamado.setCriadorEmail(escolhido.email());
        }
        return chamadoService.salvar(chamado);
    }
    @DeleteMapping("/{id}") public void excluir(@PathVariable Long id,HttpSession s){
        UsuarioSessao u=usuario(s); if(!admin(u)) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Somente administradores podem excluir chamados");
        chamadoService.excluir(id);
    }
    @PutMapping("/{id}") public Chamado atualizar(@PathVariable Long id,@Valid @RequestBody Chamado dados,HttpSession s){
        UsuarioSessao u=usuario(s); Chamado atual=autorizado(id,u);
        atual.setTitulo(dados.getTitulo()); atual.setDescricao(dados.getDescricao()); atual.setPrioridade(dados.getPrioridade());
        if(admin(u)){ UsuarioResumo escolhido=usuarioService.buscarPorEmail(dados.getCriadorEmail()); atual.setSolicitante(escolhido.nome()); atual.setSetor(escolhido.setor()==null?"":escolhido.setor()); atual.setCriadorEmail(escolhido.email()); atual.setStatus(dados.getStatus());}
        return chamadoService.salvar(atual);
    }
    @PutMapping("/{id}/status") public Chamado alterarStatus(@PathVariable Long id,@RequestParam String status,HttpSession s){
        UsuarioSessao u=usuario(s);
        Chamado chamado=autorizado(id,u);
        String novoStatus=status==null ? "" : status.trim().toUpperCase();
        if(!novoStatus.equals("ABERTO") && !novoStatus.equals("EM_ANDAMENTO") && !novoStatus.equals("FECHADO"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Status inválido");
        if(!admin(u) && novoStatus.equals("EM_ANDAMENTO"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Solicitantes podem apenas manter o chamado aberto ou fechá-lo");
        chamado.setStatus(novoStatus);
        return chamadoService.salvar(chamado);
    }
}
