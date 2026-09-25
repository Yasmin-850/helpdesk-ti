package br.com.helpdesk.helpdesk_ti;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import br.com.helpdesk.helpdesk_ti.model.Chamado;
import br.com.helpdesk.helpdesk_ti.repository.ChamadoRepository;
import br.com.helpdesk.helpdesk_ti.service.ChamadoService;

@ExtendWith(MockitoExtension.class)
class ChamadoServiceTest {

    @Mock
    private ChamadoRepository chamadoRepository;

    private ChamadoService chamadoService;

    @BeforeEach
    void setUp() {
        chamadoService = new ChamadoService(chamadoRepository);
    }

    // TESTE 1 - BUSCAR CHAMADO POR ID
    @Test
    void deveBuscarChamadoPorIdQuandoExistir() {

        Chamado chamado = new Chamado();
        chamado.setId(1L);
        chamado.setTitulo("Computador não liga");

        when(chamadoRepository.findById(1L))
                .thenReturn(Optional.of(chamado));

        Chamado resultado = chamadoService.buscarPorId(1L);

        assertEquals(1L, resultado.getId());
        assertEquals("Computador não liga", resultado.getTitulo());
    }

    // TESTE 2 - CHAMADO NÃO ENCONTRADO
    @Test
    void deveRetornarNotFoundQuandoChamadoNaoExistir() {

        when(chamadoRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> chamadoService.buscarPorId(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    // TESTE 3 - SALVAR CHAMADO
    @Test
    void deveSalvarChamado() {

        Chamado chamado = new Chamado();
        chamado.setTitulo("Impressora não imprime");
        chamado.setSolicitante("Maria");
        chamado.setSetor("Administrativo");
        chamado.setPrioridade("Alta");
        chamado.setStatus("Aberto");

        when(chamadoRepository.save(chamado))
                .thenReturn(chamado);

        Chamado resultado = chamadoService.salvar(chamado);

        assertEquals("Impressora não imprime", resultado.getTitulo());
        assertEquals("Aberto", resultado.getStatus());

        verify(chamadoRepository).save(chamado);
    }

    // TESTE 4 - LISTAR TODOS OS CHAMADOS
    @Test
    void deveListarTodosOsChamados() {

        Chamado chamado1 = new Chamado();
        chamado1.setTitulo("Computador não liga");

        Chamado chamado2 = new Chamado();
        chamado2.setTitulo("Impressora com problema");

        when(chamadoRepository.findAll())
                .thenReturn(Arrays.asList(chamado1, chamado2));

        List<Chamado> resultado = chamadoService.listarTodos();

        assertEquals(2, resultado.size());
        assertEquals("Computador não liga", resultado.get(0).getTitulo());
        assertEquals("Impressora com problema", resultado.get(1).getTitulo());
    }

    // TESTE 5 - BUSCAR POR STATUS
    @Test
    void deveBuscarChamadosPorStatus() {

        Chamado chamado = new Chamado();
        chamado.setStatus("Aberto");

        when(chamadoRepository.findByStatusIgnoreCase("Aberto"))
                .thenReturn(List.of(chamado));

        List<Chamado> resultado =
                chamadoService.buscarPorStatus("Aberto");

        assertEquals(1, resultado.size());
        assertEquals("Aberto", resultado.get(0).getStatus());
    }

    // TESTE 6 - BUSCAR POR PRIORIDADE
    @Test
    void deveBuscarChamadosPorPrioridade() {

        Chamado chamado = new Chamado();
        chamado.setPrioridade("Alta");

        when(chamadoRepository.findByPrioridadeIgnoreCase("Alta"))
                .thenReturn(List.of(chamado));

        List<Chamado> resultado =
                chamadoService.buscarPorPrioridade("Alta");

        assertEquals(1, resultado.size());
        assertEquals("Alta", resultado.get(0).getPrioridade());
    }

    // TESTE 7 - BUSCAR POR SETOR
    @Test
    void deveBuscarChamadosPorSetor() {

        Chamado chamado = new Chamado();
        chamado.setSetor("TI");

        when(chamadoRepository.findBySetorIgnoreCase("TI"))
                .thenReturn(List.of(chamado));

        List<Chamado> resultado =
                chamadoService.buscarPorSetor("TI");

        assertEquals(1, resultado.size());
        assertEquals("TI", resultado.get(0).getSetor());
    }

    // TESTE 8 - BUSCAR POR SOLICITANTE
    @Test
    void deveBuscarChamadosPorSolicitante() {

        Chamado chamado = new Chamado();
        chamado.setSolicitante("Yasmin");

        when(chamadoRepository.findBySolicitanteIgnoreCase("Yasmin"))
                .thenReturn(List.of(chamado));

        List<Chamado> resultado =
                chamadoService.buscarPorSolicitante("Yasmin");

        assertEquals(1, resultado.size());
        assertEquals("Yasmin", resultado.get(0).getSolicitante());
    }

    // TESTE 9 - ALTERAR STATUS
    @Test
    void deveAlterarStatusDoChamado() {

        Chamado chamado = new Chamado();
        chamado.setId(1L);
        chamado.setStatus("Aberto");

        when(chamadoRepository.findById(1L))
                .thenReturn(Optional.of(chamado));

        when(chamadoRepository.save(chamado))
                .thenReturn(chamado);

        Chamado resultado =
                chamadoService.alterarStatus(1L, "Resolvido");

        assertEquals("Resolvido", resultado.getStatus());

        verify(chamadoRepository).save(chamado);
    }

    // TESTE 10 - EXCLUIR CHAMADO
    @Test
    void deveExcluirChamado() {

        Chamado chamado = new Chamado();
        chamado.setId(1L);

        when(chamadoRepository.findById(1L))
                .thenReturn(Optional.of(chamado));

        chamadoService.excluir(1L);

        verify(chamadoRepository).delete(chamado);
    }

    // TESTE 11 - LISTA VAZIA
    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremChamados() {

        when(chamadoRepository.findAll())
                .thenReturn(List.of());

        List<Chamado> resultado = chamadoService.listarTodos();

        assertTrue(resultado.isEmpty());
    }
}
