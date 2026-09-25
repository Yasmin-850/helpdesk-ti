package br.com.helpdesk.helpdesk_ti.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import br.com.helpdesk.helpdesk_ti.service.UsuarioService;

@Component
public class DadosIniciais implements CommandLineRunner {
    private final UsuarioService service;
    public DadosIniciais(UsuarioService service){this.service=service;}
    @Override public void run(String... args){
        String adminSenha=System.getenv().getOrDefault("HELPDESK_ADMIN_PASSWORD","Admin@123");
        String usuarioSenha=System.getenv().getOrDefault("HELPDESK_USER_PASSWORD","Usuario@123");
        service.criarSeNaoExiste("Suporte TI","admin@helpdesk.local",adminSenha,"ADMIN","TI");
        service.criarSeNaoExiste("Usuário Teste","usuario@helpdesk.local",usuarioSenha,"SOLICITANTE","Geral");
    }
}
