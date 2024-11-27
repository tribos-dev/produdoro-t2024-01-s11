package dev.wakandaacademy.produdoro.tarefa.application.repository;

import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TarefaRepository {

    Tarefa salva(Tarefa tarefa);
    Optional<Tarefa> buscaTarefaPorId(UUID idTarefa);
    List<Tarefa> buscaTarefaPorIdUsuario(UUID idUsuario);
    void deletaTodasAsTarefas(List<Tarefa> tarefas);
    List<Tarefa> buscaTarefasConcluidas(UUID idUsuario);
    void deletaTarefasConcluidas(List<Tarefa> tarefasConcluidas);
}
