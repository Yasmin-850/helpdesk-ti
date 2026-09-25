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
import br.com.helpdesk.helpdesk_ti.model.Chamado;
import br.com.helpdesk.helpdesk_ti.service.ChamadoService;

@ExtendWith(MockitoExtension.class)
class ChamadoControllerTest {

    @Mock
    private ChamadoService chamadoService;

    private ChamadoController chamadoController;

    @BeforeEach
    void setUp() {
        chamadoController = new ChamadoController(chamadoService);
    }

    // TESTE 1 - LISTAR TODOS
    @Test
    void deveListarTodosOsChamados() {

        Chamado chamado = new Chamado();
        chamado.setId(1L);
        chamado.setTitulo("Computador não liga");
        chamado.setStatus("Aberto");

        when(chamadoService.listarTodos())
                .thenReturn(List.of(chamado));

        List<Chamado> resultado = chamadoController.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("Computador não liga", resultado.get(0).getTitulo());

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

        Chamado resultado = chamadoController.buscarPorId(1L);

        assertEquals(1L, resultado.getId());
        assertEquals("Impressora não funciona", resultado.getTitulo());

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
        chamado.setStatus("Aberto");

        when(chamadoService.salvar(chamado))
                .thenReturn(chamado);

        Chamado resultado = chamadoController.criar(chamado);

        assertEquals("Sem acesso à internet", resultado.getTitulo());
        assertEquals("Aberto", resultado.getStatus());

        verify(chamadoService).salvar(chamado);
    }

    // TESTE 4 - BUSCAR POR STATUS
    @Test
    void deveBuscarPorStatus() {

        Chamado chamado = new Chamado();
        chamado.setStatus("Aberto");

        when(chamadoService.buscarPorStatus("Aberto"))
                .thenReturn(List.of(chamado));

        List<Chamado> resultado =
                chamadoController.buscarPorStatus("Aberto");

        assertEquals(1, resultado.size());
        assertEquals("Aberto", resultado.get(0).getStatus());

        verify(chamadoService).buscarPorStatus("Aberto");
    }

    // TESTE 5 - BUSCAR POR PRIORIDADE
    @Test
    void deveBuscarPorPrioridade() {

        Chamado chamado = new Chamado();
        chamado.setPrioridade("Alta");

        when(chamadoService.buscarPorPrioridade("Alta"))
                .thenReturn(List.of(chamado));

        List<Chamado> resultado =
                chamadoController.buscarPorPrioridade("Alta");

        assertEquals(1, resultado.size());
        assertEquals("Alta", resultado.get(0).getPrioridade());

        verify(chamadoService).buscarPorPrioridade("Alta");
    }

    // TESTE 6 - BUSCAR POR SETOR
    @Test
    void deveBuscarPorSetor() {

        Chamado chamado = new Chamado();
        chamado.setSetor("TI");

        when(chamadoService.buscarPorSetor("TI"))
                .thenReturn(List.of(chamado));

        List<Chamado> resultado =
                chamadoController.buscarPorSetor("TI");

        assertEquals(1, resultado.size());
        assertEquals("TI", resultado.get(0).getSetor());

        verify(chamadoService).buscarPorSetor("TI");
    }

    // TESTE 7 - BUSCAR POR SOLICITANTE
    @Test
    void deveBuscarPorSolicitante() {

        Chamado chamado = new Chamado();
        chamado.setSolicitante("Yasmin");

        when(chamadoService.buscarPorSolicitante("Yasmin"))
                .thenReturn(List.of(chamado));

        List<Chamado> resultado =
                chamadoController.buscarPorSolicitante("Yasmin");

        assertEquals(1, resultado.size());
        assertEquals("Yasmin", resultado.get(0).getSolicitante());

        verify(chamadoService).buscarPorSolicitante("Yasmin");
    }

    // TESTE 8 - ALTERAR STATUS
    @Test
    void deveAlterarStatusDoChamado() {

        Chamado chamado = new Chamado();
        chamado.setId(1L);
        chamado.setStatus("Resolvido");

        when(chamadoService.alterarStatus(1L, "Resolvido"))
                .thenReturn(chamado);

        Chamado resultado =
                chamadoController.alterarStatus(1L, "Resolvido");

        assertEquals("Resolvido", resultado.getStatus());

        verify(chamadoService).alterarStatus(1L, "Resolvido");
    }

    // TESTE 9 - EXCLUIR CHAMADO
    @Test
    void deveExcluirChamado() {

        chamadoController.excluir(1L);

        verify(chamadoService).excluir(1L);
    }
}
