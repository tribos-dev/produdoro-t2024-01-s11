package dev.wakandaacademy.produdoro.tarefa.application.repository;

import dev.wakandaacademy.produdoro.tarefa.application.api.NovaPosicaoRequest;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TarefaRepository {

    Tarefa salva(Tarefa tarefa);
    Optional<Tarefa> buscaTarefaPorId(UUID idTarefa);
    void inativaTarefa(UUID idUsuario);
    List<Tarefa> buscaTarefaPorIdUsuario(UUID idUsuario);
    Integer contagemPosicao(UUID idUsuario);
    void defineNovaPosicaoTarefa(Tarefa tarefa, List<Tarefa> todasTarefas, NovaPosicaoRequest novaPosicao);
    void deletaTodasAsTarefas(List<Tarefa> tarefas);
    List<Tarefa> buscaTarefasConcluidas(UUID idUsuario);
    void deletaTarefasConcluidas(List<Tarefa> tarefasConcluidas);
}
