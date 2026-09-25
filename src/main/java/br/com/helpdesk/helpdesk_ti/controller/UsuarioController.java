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

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // =========================================================
    // USUÁRIO DA SESSÃO
    // =========================================================

    private UsuarioSessao sessao(HttpSession session) {

        UsuarioSessao usuario =
                (UsuarioSessao) session.getAttribute("usuario");

        if (usuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Não autenticado"
            );
        }

        return usuario;
    }

    // =========================================================
    // VERIFICA SE É ADMINISTRADOR
    // =========================================================

    private boolean admin(UsuarioSessao usuario) {
        return usuario != null
                && "ADMIN".equalsIgnoreCase(usuario.perfil());
    }

    // =========================================================
    // EXIGE PERFIL ADMINISTRADOR
    // =========================================================

    private void exigirAdmin(HttpSession session) {

        UsuarioSessao usuario = sessao(session);

        if (!admin(usuario)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Acesso exclusivo do administrador"
            );
        }
    }

    // =========================================================
    // LISTAR USUÁRIOS
    // SOMENTE ADMINISTRADOR
    // =========================================================

    @GetMapping
    public List<UsuarioResumo> listar(HttpSession session) {

        exigirAdmin(session);

        return service.listar();
    }

    // =========================================================
    // CONSULTAR MEU PERFIL
    // ADMINISTRADOR E SOLICITANTE
    // =========================================================

    @GetMapping("/me")
    public UsuarioResumo meuPerfil(HttpSession session) {

        UsuarioSessao usuario = sessao(session);

        return service.buscarResumo(usuario.id());
    }

    // =========================================================
    // EDITAR MEU PERFIL
    // SOMENTE ADMINISTRADOR
    // =========================================================

    @PutMapping("/me")
    public UsuarioResumo editarMeuPerfil(
            @RequestBody EditarUsuarioRequest request,
            HttpSession session) {

        UsuarioSessao usuario = sessao(session);

        /*
         * CORREÇÃO:
         *
         * O solicitante pode visualizar seus dados,
         * mas não pode alterar:
         *
         * - Nome
         * - E-mail
         * - Setor
         *
         * Essas informações devem ser administradas
         * pelo perfil ADMIN.
         */

        if (!admin(usuario)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solicitantes não podem alterar os dados do próprio perfil"
            );
        }

        UsuarioResumo atualizado =
                service.editarMeuPerfil(
                        usuario.id(),
                        request.nome(),
                        request.email(),
                        request.setor()
                );

        /*
         * Atualiza os dados armazenados na sessão
         * depois da alteração realizada pelo administrador.
         */

        session.setAttribute(
                "usuario",
                new UsuarioSessao(
                        atualizado.id(),
                        atualizado.nome(),
                        atualizado.email(),
                        atualizado.perfil(),
                        atualizado.setor()
                )
        );

        return atualizado;
    }

    // =========================================================
    // CRIAR USUÁRIO
    // SOMENTE ADMINISTRADOR
    // =========================================================

    @PostMapping
    public UsuarioResumo criar(
            @RequestBody NovoUsuarioRequest request,
            HttpSession session) {

        exigirAdmin(session);

        return service.criar(
                request.nome(),
                request.email(),
                request.senha(),
                request.perfil(),
                request.setor()
        );
    }

    // =========================================================
    // BUSCAR USUÁRIO POR ID
    // SOMENTE ADMINISTRADOR
    // =========================================================

    @GetMapping("/{id}")
    public UsuarioResumo buscar(
            @PathVariable Long id,
            HttpSession session) {

        exigirAdmin(session);

        return service.buscarResumo(id);
    }

    // =========================================================
    // EDITAR USUÁRIO
    // SOMENTE ADMINISTRADOR
    // =========================================================

    @PutMapping("/{id}")
    public UsuarioResumo editar(
            @PathVariable Long id,
            @RequestBody EditarUsuarioRequest request,
            HttpSession session) {

        UsuarioSessao usuario = sessao(session);

        exigirAdmin(session);

        return service.editar(
                id,
                request.nome(),
                request.email(),
                request.perfil(),
                request.setor(),
                usuario.id()
        );
    }

    // =========================================================
    // EXCLUIR USUÁRIO
    // SOMENTE ADMINISTRADOR
    // =========================================================

    @DeleteMapping("/{id}")
    public void excluir(
            @PathVariable Long id,
            HttpSession session) {

        UsuarioSessao usuario = sessao(session);

        exigirAdmin(session);

        service.excluir(
                id,
                usuario.id()
        );
    }

    // =========================================================
    // ALTERAR MINHA SENHA
    // ADMINISTRADOR E SOLICITANTE
    // =========================================================

    @PutMapping("/minha-senha")
    public void alterarSenha(
            @RequestBody AlterarSenhaRequest request,
            HttpSession session) {

        UsuarioSessao usuario = sessao(session);

        /*
         * A troca da própria senha continua permitida.
         *
         * Isso não permite alterar nome, e-mail,
         * setor ou perfil.
         */

        service.alterarSenha(
                usuario.id(),
                request.senhaAtual(),
                request.novaSenha()
        );
    }
}
