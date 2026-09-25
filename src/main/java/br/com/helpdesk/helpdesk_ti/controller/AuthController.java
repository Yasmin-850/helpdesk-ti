package br.com.helpdesk.helpdesk_ti.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import br.com.helpdesk.helpdesk_ti.dto.LoginRequest;
import br.com.helpdesk.helpdesk_ti.dto.UsuarioSessao;
import br.com.helpdesk.helpdesk_ti.model.Usuario;
import br.com.helpdesk.helpdesk_ti.service.UsuarioService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UsuarioService service;
    public AuthController(UsuarioService service){this.service=service;}
    @PostMapping("/login")
    public UsuarioSessao login(@RequestBody LoginRequest req, HttpSession session){
        Usuario u=service.autenticar(req.email(), req.senha());
        if(u==null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"E-mail ou senha inválidos");
        UsuarioSessao us=new UsuarioSessao(u.getId(),u.getNome(),u.getEmail(),u.getPerfil(),u.getSetor());
        session.setAttribute("usuario",us); return us;
    }
    @GetMapping("/me")
    public UsuarioSessao me(HttpSession session){
        UsuarioSessao u=(UsuarioSessao)session.getAttribute("usuario");
        if(u==null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Não autenticado");
        return u;
    }
    @PostMapping("/logout") public void logout(HttpSession session){session.invalidate();}
}
