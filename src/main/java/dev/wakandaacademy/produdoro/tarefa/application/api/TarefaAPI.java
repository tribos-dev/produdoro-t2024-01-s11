package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/tarefa")
public interface TarefaAPI {
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    TarefaIdResponse postNovaTarefa(@RequestBody @Valid TarefaRequest tarefaRequest);

    @GetMapping("/{idTarefa}")
    @ResponseStatus(code = HttpStatus.OK)
    TarefaDetalhadoResponse detalhaTarefa(@RequestHeader(name = "Authorization",required = true) String token, 
    		@PathVariable UUID idTarefa);

    @PatchMapping("/conclui-tarefa/{idTarefa}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void concluiTarefa(@RequestHeader(name = "Authorization", required = true) String token, @PathVariable UUID idTarefa);

    @GetMapping("/listar-tarefas/{idUsuario}")
    @ResponseStatus(code = HttpStatus.OK)
    List<TarefaListResponse> listarTodasAsTarefas(@RequestHeader(name = "Authorization",required = true) String token,
                                                  @PathVariable UUID idUsuario);

    @DeleteMapping("/limpar-tarefas/{idUsuario}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void deletaTodasAsTarefas(@RequestHeader(name = "Authorization",required = true) String token,
                              @PathVariable UUID idUsuario);
                              
    @DeleteMapping("/{idUsuario}/deleta-tarefas-concluidas")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void deletaTarefasConcluidas(@RequestHeader(name = "Authorization",required = true) String token, @PathVariable UUID idUsuario);

}
