package br.com.helpdesk.helpdesk_ti.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import br.com.helpdesk.helpdesk_ti.dto.UsuarioSessao;
import jakarta.servlet.http.HttpSession;

@Controller
public class PaginaController {
    @GetMapping("/") public String paginaInicial(HttpSession session){
        UsuarioSessao u=(UsuarioSessao)session.getAttribute("usuario");
        return u==null?"redirect:/login":"index";
    }
    @GetMapping("/login") public String login(HttpSession session){
        return session.getAttribute("usuario")!=null?"redirect:/":"login";
    }
    @GetMapping("/usuarios") public String usuarios(HttpSession session){
        UsuarioSessao u=(UsuarioSessao)session.getAttribute("usuario");
        if(u==null) return "redirect:/login";
        return "ADMIN".equals(u.perfil()) ? "usuarios" : "redirect:/";
    }
}
