package br.com.helpdesk.helpdesk_ti.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import br.com.helpdesk.helpdesk_ti.dto.UsuarioResumo;
import br.com.helpdesk.helpdesk_ti.dto.UsuarioSessao;
import br.com.helpdesk.helpdesk_ti.model.Chamado;
import br.com.helpdesk.helpdesk_ti.service.ChamadoService;
import br.com.helpdesk.helpdesk_ti.service.UsuarioService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/chamados")
public class ChamadoController {

    private final ChamadoService chamadoService;
    private final UsuarioService usuarioService;

    public ChamadoController(
            ChamadoService chamadoService,
            UsuarioService usuarioService) {

        this.chamadoService = chamadoService;
        this.usuarioService = usuarioService;
    }

    // =========================================================
    // USUÁRIO DA SESSÃO
    // =========================================================

    private UsuarioSessao usuario(HttpSession s) {

        UsuarioSessao usuario =
                (UsuarioSessao) s.getAttribute("usuario");

        if (usuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado"
            );
        }

        return usuario;
    }

    // =========================================================
    // VERIFICA SE É ADMINISTRADOR
    // =========================================================

    private boolean admin(UsuarioSessao u) {
        return u != null && "ADMIN".equalsIgnoreCase(u.perfil());
    }

    // =========================================================
    // VERIFICA SE O USUÁRIO PODE ACESSAR O CHAMADO
    // =========================================================

    private Chamado autorizado(Long id, UsuarioSessao u) {

        Chamado chamado = chamadoService.buscarPorId(id);

        // Administrador pode acessar qualquer chamado
        if (admin(u)) {
            return chamado;
        }

        // Solicitante só pode acessar seus próprios chamados
        if (chamado.getCriadorEmail() == null
                || !chamado.getCriadorEmail()
                        .equalsIgnoreCase(u.email())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Sem permissão para acessar este chamado"
            );
        }

        return chamado;
    }

    // =========================================================
    // LISTAR CHAMADOS
    // =========================================================

    @GetMapping
    public List<Chamado> listarTodos(HttpSession s) {

        UsuarioSessao u = usuario(s);

        // Administrador visualiza todos
        if (admin(u)) {
            return chamadoService.listarTodos();
        }

        // Solicitante visualiza somente os próprios chamados
        return chamadoService.buscarPorCriadorEmail(u.email());
    }

    // =========================================================
    // BUSCAR CHAMADO POR ID
    // =========================================================

    @GetMapping("/{id}")
    public Chamado buscarPorId(
            @PathVariable Long id,
            HttpSession s) {

        return autorizado(id, usuario(s));
    }

    // =========================================================
    // BUSCAR POR STATUS
    // =========================================================

    @GetMapping("/status/{status}")
    public List<Chamado> buscarPorStatus(
            @PathVariable String status,
            HttpSession s) {

        return listarTodos(s)
                .stream()
                .filter(c ->
                        status.equalsIgnoreCase(c.getStatus()))
                .toList();
    }

    // =========================================================
    // BUSCAR POR PRIORIDADE
    // =========================================================

    @GetMapping("/prioridade/{prioridade}")
    public List<Chamado> buscarPorPrioridade(
            @PathVariable String prioridade,
            HttpSession s) {

        return listarTodos(s)
                .stream()
                .filter(c ->
                        prioridade.equalsIgnoreCase(
                                c.getPrioridade()))
                .toList();
    }

    // =========================================================
    // BUSCAR POR SETOR
    // =========================================================

    @GetMapping("/setor/{setor}")
    public List<Chamado> buscarPorSetor(
            @PathVariable String setor,
            HttpSession s) {

        return listarTodos(s)
                .stream()
                .filter(c ->
                        setor.equalsIgnoreCase(c.getSetor()))
                .toList();
    }

    // =========================================================
    // BUSCAR POR SOLICITANTE
    // =========================================================

    @GetMapping("/solicitante/{solicitante}")
    public List<Chamado> buscarPorSolicitante(
            @PathVariable String solicitante,
            HttpSession s) {

        return listarTodos(s)
                .stream()
                .filter(c ->
                        solicitante.equalsIgnoreCase(
                                c.getSolicitante()))
                .toList();
    }

    // =========================================================
    // CRIAR CHAMADO
    // =========================================================

    @PostMapping
    public Chamado criar(
            @Valid @RequestBody Chamado chamado,
            HttpSession s) {

        UsuarioSessao u = usuario(s);

        // Impede alteração manual do ID
        chamado.setId(null);

        // -----------------------------------------------------
        // SOLICITANTE
        // -----------------------------------------------------

        if (!admin(u)) {

            chamado.setCriadorEmail(u.email());
            chamado.setSolicitante(u.nome());

            if (u.setor() != null && !u.setor().isBlank()) {
                chamado.setSetor(u.setor());
            }

            // Todo novo chamado criado pelo solicitante
            // começa obrigatoriamente como ABERTO
            chamado.setStatus("ABERTO");
        }

        // -----------------------------------------------------
        // ADMINISTRADOR
        // -----------------------------------------------------

        else {

            if (chamado.getCriadorEmail() == null
                    || chamado.getCriadorEmail().isBlank()) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Selecione um solicitante cadastrado"
                );
            }

            UsuarioResumo escolhido =
                    usuarioService.buscarPorEmail(
                            chamado.getCriadorEmail());

            chamado.setSolicitante(escolhido.nome());

            chamado.setSetor(
                    escolhido.setor() == null
                            ? ""
                            : escolhido.setor()
            );

            chamado.setCriadorEmail(escolhido.email());

            // Se o administrador não informar status,
            // o chamado começa como ABERTO
            if (chamado.getStatus() == null
                    || chamado.getStatus().isBlank()) {

                chamado.setStatus("ABERTO");
            }
        }

        return chamadoService.salvar(chamado);
    }

    // =========================================================
    // ATUALIZAR CHAMADO
    // =========================================================

    @PutMapping("/{id}")
    public Chamado atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Chamado dados,
            HttpSession s) {

        UsuarioSessao u = usuario(s);

        Chamado atual = autorizado(id, u);

        /*
         * Tanto administrador quanto o dono do chamado
         * podem atualizar estas informações.
         *
         * O solicitante NÃO consegue alterar:
         * - status
         * - solicitante
         * - criadorEmail
         */

        atual.setTitulo(dados.getTitulo());
        atual.setDescricao(dados.getDescricao());
        atual.setPrioridade(dados.getPrioridade());

        // -----------------------------------------------------
        // CAMPOS EXCLUSIVOS DO ADMINISTRADOR
        // -----------------------------------------------------

        if (admin(u)) {

            if (dados.getCriadorEmail() == null
                    || dados.getCriadorEmail().isBlank()) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Selecione um solicitante cadastrado"
                );
            }

            UsuarioResumo escolhido =
                    usuarioService.buscarPorEmail(
                            dados.getCriadorEmail());

            atual.setSolicitante(escolhido.nome());

            atual.setSetor(
                    escolhido.setor() == null
                            ? ""
                            : escolhido.setor()
            );

            atual.setCriadorEmail(escolhido.email());

            /*
             * Mantemos o status atual neste endpoint.
             * A alteração de status deve ocorrer exclusivamente
             * pelo endpoint /{id}/status.
             *
             * Isso evita alterar status acidentalmente durante
             * a edição normal do chamado.
             */
        }

        return chamadoService.salvar(atual);
    }

    // =========================================================
    // ALTERAR STATUS
    // SOMENTE ADMINISTRADOR
    // =========================================================

    @PutMapping("/{id}/status")
    public Chamado alterarStatus(
            @PathVariable Long id,
            @RequestParam String status,
            HttpSession s) {

        UsuarioSessao u = usuario(s);

        /*
         * CORREÇÃO:
         *
         * Solicitantes não podem alterar o status.
         *
         * Portanto, não podem:
         * - colocar EM_ANDAMENTO
         * - FECHAR
         * - REABRIR
         */

        if (!admin(u)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Somente administradores podem alterar o status dos chamados"
            );
        }

        Chamado chamado = chamadoService.buscarPorId(id);

        String novoStatus =
                status == null
                        ? ""
                        : status.trim().toUpperCase();

        // Validação dos status permitidos
        if (!novoStatus.equals("ABERTO")
                && !novoStatus.equals("EM_ANDAMENTO")
                && !novoStatus.equals("FECHADO")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status inválido"
            );
        }

        chamado.setStatus(novoStatus);

        return chamadoService.salvar(chamado);
    }

    // =========================================================
    // EXCLUIR CHAMADO
    // SOMENTE ADMINISTRADOR
    // =========================================================

    @DeleteMapping("/{id}")
    public void excluir(
            @PathVariable Long id,
            HttpSession s) {

        UsuarioSessao u = usuario(s);

        if (!admin(u)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Somente administradores podem excluir chamados"
            );
        }

        chamadoService.excluir(id);
    }
}
