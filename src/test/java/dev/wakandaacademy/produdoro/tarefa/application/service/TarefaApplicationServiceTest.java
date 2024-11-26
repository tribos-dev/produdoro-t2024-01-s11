package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.NovaPosicaoRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
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
        when(tarefaRepository.salva(any())).thenReturn(new Tarefa(request, 2));

        TarefaIdResponse response = tarefaApplicationService.criaNovaTarefa(request);

        assertNotNull(response);
        assertEquals(TarefaIdResponse.class, response.getClass());
        assertEquals(UUID.class, response.getIdTarefa().getClass());
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
