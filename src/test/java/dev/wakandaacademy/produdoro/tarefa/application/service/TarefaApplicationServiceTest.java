package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.NovaPosicaoRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

@ExtendWith(MockitoExtension.class)
class TarefaApplicationServiceTest {

    //	@Autowired
    @InjectMocks
    TarefaApplicationService tarefaApplicationService;

    //	@MockBean
    @Mock
    TarefaRepository tarefaRepository;

    //	@MockBean
    @Mock
    UsuarioRepository usuarioRepository;

    @Test
    void deveRetornarIdTarefaNovaCriada() {
        TarefaRequest request = getTarefaRequest();
        when(tarefaRepository.salva(any())).thenReturn(new Tarefa(request, 2));

        TarefaIdResponse response = tarefaApplicationService.criaNovaTarefa(request);

        assertNotNull(response);
        assertEquals(TarefaIdResponse.class, response.getClass());
        assertEquals(UUID.class, response.getIdTarefa().getClass());
    }

    @Test
    void deveEditarTarefa(){
        Usuario usuario = DataHelper.createUsuario();
        Tarefa tarefa = DataHelper.createTarefa();
        EditaTarefaRequest editaTarefaRequest = DataHelper.createEditaTarefa();
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));
        tarefaApplicationService.editaTarefa(usuario.getEmail(), tarefa.getIdTarefa(), editaTarefaRequest);
        verify(usuarioRepository, times(1)).buscaUsuarioPorEmail(usuario.getEmail());
        verify(tarefaRepository, times(1)).buscaTarefaPorId(tarefa.getIdTarefa());
        assertEquals("TAREFA 2", tarefa.getDescricao());
    }

    @Test
    void naoDeveEditarTarefa(){
        UUID idTarefaInvalido = UUID.randomUUID();
        String usuario = "Allan";
        EditaTarefaRequest editaTarefaRequest = DataHelper.createEditaTarefa();
        when(tarefaRepository.buscaTarefaPorId(idTarefaInvalido)).thenReturn(Optional.empty());
        assertThrows(APIException.class,
                () -> tarefaApplicationService.editaTarefa(usuario, idTarefaInvalido, editaTarefaRequest));
        verify(tarefaRepository, times(1)).buscaTarefaPorId(idTarefaInvalido);
    }

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

    void deveConcluirTarefa() {
        Usuario usuario = DataHelper.createUsuario();
        Tarefa tarefa = DataHelper.createTarefa();
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));
        tarefaApplicationService.concluiTarefa(usuario.getEmail(), tarefa.getIdTarefa());
        assertEquals(tarefa.getStatus(), StatusTarefa.CONCLUIDA);
    }

    @Test
    void deveListarTarefasdoUsuario(){
        Usuario usuario = DataHelper.createUsuario();
        List<Tarefa> listaTarefas = DataHelper.createListTarefa();
        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorIdUsuario(any())).thenReturn(listaTarefas);
        String usuarioEmail = "email@email.com";
        UUID idUsuario = UUID.fromString("a713162f-20a9-4db9-a85b-90cd51ab18f4");
        List<TarefaListResponse> response = tarefaApplicationService.buscarTodasAsTarefas(usuarioEmail, idUsuario);
        assertNotNull(response);
        assertEquals(ArrayList.class,response.getClass());
        assertEquals(8, response.size());
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
    
    @Test
    void deletaTarefasSucesso() {
        Usuario usuario = DataHelper.createUsuario();
        List<Tarefa> tarefasDoUsuario = DataHelper.createListTarefa();
        String email = usuario.getEmail();
        UUID idUsuario = usuario.getIdUsuario();
        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorIdUsuario(any())).thenReturn(tarefasDoUsuario);
        tarefaApplicationService.deletaTodasAsTarefas(email, idUsuario);
        verify(tarefaRepository, times(1)).deletaTodasAsTarefas(tarefasDoUsuario);
    }

    @Test
    void deletaTarefasConcluidasSucesso() {
        Usuario usuario = DataHelper.createUsuario();
        List<Tarefa> listaTarefas = DataHelper.createListTarefa();
        String email = usuario.getEmail();
        UUID idUsuario = usuario.getIdUsuario();
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefasConcluidas(any())).thenReturn(listaTarefas);
        tarefaApplicationService.deletaTarefasConcluidas(email, idUsuario);
        verify(tarefaRepository, times(1)).deletaTarefasConcluidas(listaTarefas);
    }
    
    @Test
    void incrementaPomodoro() {
    	Usuario usuario = DataHelper.createUsuario();
    	Tarefa tarefa = DataHelper.createTarefa();
    	int contagemPomodoroInicio = tarefa.getContagemPomodoro();
    	
    	when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
    	when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));
    	tarefaApplicationService.incrementaPomodoro(usuario.getEmail(), tarefa.getIdTarefa());
    	int contagemPomodoroFinal = tarefa.getContagemPomodoro();
    	verify(tarefaRepository, times(1)).salva(any());
    	assertEquals(contagemPomodoroInicio + 1, contagemPomodoroFinal);
     }

    public TarefaRequest getTarefaRequest() {
        TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0);
        return request;
    }

    @Test
    void alteraPosicaoTarefa_deveAlterarPosicaDaTarefa(){
        Usuario usuarioMock = DataHelper.createUsuario();
        Tarefa tarefaMock = DataHelper.createTarefa();

        NovaPosicaoRequest novaPosicaoRequest = new NovaPosicaoRequest(0);
        List<Tarefa> tarefasMock = DataHelper.createListTarefa();

        when(usuarioRepository.buscaUsuarioPorEmail("usuario@email.com")).thenReturn(usuarioMock);
        when(tarefaRepository.buscaTarefaPorId(tarefaMock.getIdTarefa())).thenReturn(Optional.of(tarefaMock));
        when(tarefaRepository.buscaTarefaPorIdUsuario(usuarioMock.getIdUsuario())).thenReturn(tarefasMock);

        tarefaApplicationService.alteraPosicaoTarefa("usuario@email.com", tarefaMock.getIdTarefa(), novaPosicaoRequest);

        verify(tarefaRepository).defineNovaPosicaoTarefa(tarefaMock, tarefasMock, novaPosicaoRequest);
    }
    @Test
    void alteraPosicaoTarefa_idDaTarefaInvalido(){
        Usuario usuarioMock = DataHelper.createUsuario();
        UUID idTarefaInvalido = UUID.randomUUID();

        NovaPosicaoRequest novaPosicaoRequest = new NovaPosicaoRequest(0);

        when(usuarioRepository.buscaUsuarioPorEmail("usuario@email.com")).thenReturn(usuarioMock);
        doThrow(APIException.build(HttpStatus.NOT_FOUND,
                "Tarefa não encontrada!")).when(tarefaRepository).buscaTarefaPorId(idTarefaInvalido);

        assertThrows(APIException.class, () ->tarefaApplicationService.alteraPosicaoTarefa
                ("usuario@email.com", idTarefaInvalido, novaPosicaoRequest));

        verify(tarefaRepository, times(1)).buscaTarefaPorId(idTarefaInvalido);
    }
}
