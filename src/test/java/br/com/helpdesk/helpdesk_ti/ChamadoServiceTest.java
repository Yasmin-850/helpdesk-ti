package br.com.helpdesk.helpdesk_ti;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

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
}
