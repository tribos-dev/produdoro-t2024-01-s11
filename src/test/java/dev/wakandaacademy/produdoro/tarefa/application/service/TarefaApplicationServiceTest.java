package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TarefaApplicationServiceTest {

    //	@Autowired
    @InjectMocks
    TarefaApplicationService tarefaApplicationService;

    //	@MockBean
    @Mock
    TarefaRepository tarefaRepository;

    @Mock
    UsuarioRepository usuarioRepository;

    @Test
    void deveRetornarIdTarefaNovaCriada() {
        TarefaRequest request = getTarefaRequest();
        when(tarefaRepository.salva(any())).thenReturn(new Tarefa(request));

        TarefaIdResponse response = tarefaApplicationService.criaNovaTarefa(request);

        assertNotNull(response);
        assertEquals(TarefaIdResponse.class, response.getClass());
        assertEquals(UUID.class, response.getIdTarefa().getClass());
    }

    @Test
    void deveAtivarTarefaComSucesso() {
        UUID idTarefa = DataHelper.createTarefa().getIdTarefa();
        Tarefa tarefa = DataHelper.createTarefa();
        Usuario usuario = DataHelper.createUsuario();
        String email = usuario.getEmail();
        when(tarefaRepository.buscaTarefaPorId(idTarefa)).thenReturn(Optional.of(tarefa));
        when(usuarioRepository.buscaUsuarioPorEmail(email)).thenReturn(usuario);
        tarefaApplicationService.ativaTarefa(email, idTarefa);
        verify(tarefaRepository).inativaTarefa(usuario.getIdUsuario());
        verify(tarefaRepository).salva(tarefa);
        assertEquals(StatusAtivacaoTarefa.ATIVA, tarefa.getStatusAtivacao());
    }

    @Test
    void deveLancarExeptionSeTarefaNaoExistir() {
        UUID idTarefa = UUID.randomUUID();
        String email = "email@email.com";
        when(tarefaRepository.buscaTarefaPorId(idTarefa)).thenReturn(Optional.empty());
        APIException ex = assertThrows(APIException.class, () ->
            tarefaApplicationService.ativaTarefa(email, idTarefa));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusException());
        assertEquals("Id da tarefa inválido!", ex.getMessage());
    }

    @Test
    void deveLancarExceptionSeUsuarioNaoForDonoDaTarefa() {
        Tarefa tarefa = DataHelper.createTarefa();
        UUID idTarefa = tarefa.getIdTarefa();
        Usuario usuario = DataHelper.createUsuario();
        Usuario outroUsuario = Usuario.builder().idUsuario(UUID.randomUUID()).email("outro@email.com").build();
        String email = outroUsuario.getEmail();
        when(tarefaRepository.buscaTarefaPorId(idTarefa)).thenReturn(Optional.of(tarefa));
        when(usuarioRepository.buscaUsuarioPorEmail(email)).thenReturn(outroUsuario);
        APIException ex = assertThrows(APIException.class, () ->
                tarefaApplicationService.ativaTarefa(email, idTarefa));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusException());
        assertEquals("Token não corresponde ao dono da tarefa!", ex.getMessage());
    }

    @Test
    void deveLancarExceptionSeTarefaJaEstiverAtiva() {
        Usuario usuario = DataHelper.createUsuario();
        String email = usuario.getEmail();
        Tarefa tarefa = Tarefa.builder().contagemPomodoro(1).idTarefa(UUID.fromString("06fb5521-9d5a-461a-82fb-e67e3bedc6eb"))
                .idUsuario(usuario.getIdUsuario()).descricao("descricao tarefa").statusAtivacao(StatusAtivacaoTarefa.ATIVA).build();
        UUID idTarefa = tarefa.getIdTarefa();

        when(tarefaRepository.buscaTarefaPorId(idTarefa)).thenReturn(Optional.of(tarefa));
        when(usuarioRepository.buscaUsuarioPorEmail(email)).thenReturn(usuario);

        APIException ex = assertThrows(APIException.class, () ->
                tarefaApplicationService.ativaTarefa(email, idTarefa));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusException());
        assertEquals("Tarefa já está ativa!", ex.getMessage());
    }

    public TarefaRequest getTarefaRequest() {
        TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0);
        return request;
    }
}
