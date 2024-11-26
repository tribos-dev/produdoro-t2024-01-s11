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
    
    @PostMapping("/incrementaPomodoro/{idTarefa}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void incrementaPomodoro(@RequestHeader("Authorization") String token, @PathVariable UUID idTarefa);

    @GetMapping("/{idTarefa}")
    @ResponseStatus(code = HttpStatus.OK)
    TarefaDetalhadoResponse detalhaTarefa(@RequestHeader(name = "Authorization",required = true) String token, 
    		@PathVariable UUID idTarefa);


    @PatchMapping("/ativaTarefa/{idTarefa}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void ativaTarefa(@RequestHeader(name = "Authorization",required = true) String token,
            @PathVariable UUID idTarefa);


    @GetMapping("/listar-tarefas/{idUsuario}")
    @ResponseStatus(code = HttpStatus.OK)
    List<TarefaListResponse> listarTodasAsTarefas(@RequestHeader(name = "Authorization",required = true) String token,
                                                  @PathVariable UUID idUsuario);

}
