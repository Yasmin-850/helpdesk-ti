package br.com.helpdesk.helpdesk_ti;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.helpdesk.helpdesk_ti.controller.ChamadoController;
import br.com.helpdesk.helpdesk_ti.dto.UsuarioSessao;
import br.com.helpdesk.helpdesk_ti.model.Chamado;
import br.com.helpdesk.helpdesk_ti.service.ChamadoService;
import br.com.helpdesk.helpdesk_ti.service.UsuarioService;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
class ChamadoControllerTest {

    @Mock
    private ChamadoService chamadoService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private HttpSession session;

    private ChamadoController chamadoController;

    @BeforeEach
    void setUp() {

        chamadoController =
                new ChamadoController(chamadoService, usuarioService);

        UsuarioSessao admin =
                new UsuarioSessao(
                        "Administrador",
                        "admin@helpdesk.com",
                        "TI",
                        "ADMIN"
                );

        when(session.getAttribute("usuario"))
                .thenReturn(admin);
    }

    // TESTE 1 - LISTAR TODOS
    @Test
    void deveListarTodosOsChamados() {

        Chamado chamado = new Chamado();
        chamado.setId(1L);
        chamado.setTitulo("Computador não liga");
        chamado.setStatus("ABERTO");

        when(chamadoService.listarTodos())
                .thenReturn(List.of(chamado));

        List<Chamado> resultado =
                chamadoController.listarTodos(session);

        assertEquals(1, resultado.size());
        assertEquals(
                "Computador não liga",
                resultado.get(0).getTitulo()
        );

        verify(chamadoService).listarTodos();
    }

    // TESTE 2 - BUSCAR POR ID
    @Test
    void deveBuscarChamadoPorId() {

        Chamado chamado = new Chamado();
        chamado.setId(1L);
        chamado.setTitulo("Impressora não funciona");

        when(chamadoService.buscarPorId(1L))
                .thenReturn(chamado);

        Chamado resultado =
                chamadoController.buscarPorId(1L, session);

        assertEquals(1L, resultado.getId());
        assertEquals(
                "Impressora não funciona",
                resultado.getTitulo()
        );

        verify(chamadoService).buscarPorId(1L);
    }

    // TESTE 3 - CRIAR CHAMADO
    @Test
    void deveCriarChamado() {

        Chamado chamado = new Chamado();

        chamado.setTitulo("Sem acesso à internet");
        chamado.setSolicitante("Yasmin");
        chamado.setSetor("TI");
        chamado.setPrioridade("Alta");
        chamado.setStatus("ABERTO");
        chamado.setCriadorEmail("yasmin@helpdesk.com");

        br.com.helpdesk.helpdesk_ti.dto.UsuarioResumo usuario =
                new br.com.helpdesk.helpdesk_ti.dto.UsuarioResumo(
                        "Yasmin",
                        "yasmin@helpdesk.com",
                        "TI"
                );

        when(usuarioService.buscarPorEmail(
                "yasmin@helpdesk.com"))
                .thenReturn(usuario);

        when(chamadoService.salvar(chamado))
                .thenReturn(chamado);

        Chamado resultado =
                chamadoController.criar(chamado, session);

        assertEquals(
                "Sem acesso à internet",
                resultado.getTitulo()
        );

        assertEquals(
                "yasmin@helpdesk.com",
                resultado.getCriadorEmail()
        );

        verify(chamadoService).salvar(chamado);
    }

    // TESTE 4 - BUSCAR POR STATUS
    @Test
    void deveBuscarPorStatus() {

        Chamado chamado = new Chamado();
        chamado.setStatus("ABERTO");

        when(chamadoService.listarTodos())
                .thenReturn(List.of(chamado));

        List<Chamado> resultado =
                chamadoController.buscarPorStatus(
                        "ABERTO",
                        session
                );

        assertEquals(1, resultado.size());
        assertEquals(
                "ABERTO",
                resultado.get(0).getStatus()
        );
    }

    // TESTE 5 - BUSCAR POR PRIORIDADE
    @Test
    void deveBuscarPorPrioridade() {

        Chamado chamado = new Chamado();
        chamado.setPrioridade("Alta");

        when(chamadoService.listarTodos())
                .thenReturn(List.of(chamado));

        List<Chamado> resultado =
                chamadoController.buscarPorPrioridade(
                        "Alta",
                        session
                );

        assertEquals(1, resultado.size());
        assertEquals(
                "Alta",
                resultado.get(0).getPrioridade()
        );
    }

    // TESTE 6 - BUSCAR POR SETOR
    @Test
    void deveBuscarPorSetor() {

        Chamado chamado = new Chamado();
        chamado.setSetor("TI");

        when(chamadoService.listarTodos())
                .thenReturn(List.of(chamado));

        List<Chamado> resultado =
                chamadoController.buscarPorSetor(
                        "TI",
                        session
                );

        assertEquals(1, resultado.size());
        assertEquals(
                "TI",
                resultado.get(0).getSetor()
        );
    }

    // TESTE 7 - BUSCAR POR SOLICITANTE
    @Test
    void deveBuscarPorSolicitante() {

        Chamado chamado = new Chamado();
        chamado.setSolicitante("Yasmin");

        when(chamadoService.listarTodos())
                .thenReturn(List.of(chamado));

        List<Chamado> resultado =
                chamadoController.buscarPorSolicitante(
                        "Yasmin",
                        session
                );

        assertEquals(1, resultado.size());
        assertEquals(
                "Yasmin",
                resultado.get(0).getSolicitante()
        );
    }

    // TESTE 8 - ALTERAR STATUS
    @Test
    void deveAlterarStatusDoChamado() {

        Chamado chamado = new Chamado();

        chamado.setId(1L);
        chamado.setStatus("ABERTO");

        when(chamadoService.buscarPorId(1L))
                .thenReturn(chamado);

        when(chamadoService.salvar(chamado))
                .thenReturn(chamado);

        Chamado resultado =
                chamadoController.alterarStatus(
                        1L,
                        "FECHADO",
                        session
                );

        assertEquals(
                "FECHADO",
                resultado.getStatus()
        );

        verify(chamadoService).salvar(chamado);
    }

    // TESTE 9 - EXCLUIR CHAMADO
    @Test
    void deveExcluirChamado() {

        chamadoController.excluir(
                1L,
                session
        );

        verify(chamadoService).excluir(1L);
    }
}
