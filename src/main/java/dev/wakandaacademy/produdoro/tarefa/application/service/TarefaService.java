package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

import java.util.List;
import java.util.UUID;

public interface TarefaService {

  TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest);
  Tarefa detalhaTarefa(String usuario, UUID idTarefa);
  void editaTarefa(String emailUsuario, UUID idTarefa, EditaTarefaRequest tarefaRequest);
	TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest);
	Tarefa detalhaTarefa(String usuario, UUID idTarefa);
	void incrementaPomodoro(String usuarioEmail, UUID idTarefa);
	void ativaTarefa(String email, UUID idTarefa);
	List<TarefaListResponse> buscarTodasAsTarefas(String usuario, UUID idUsuario);
	void deletaTarefasConcluidas(String usuarioEmail, UUID idUsuario);
	void concluiTarefa(String usuarioEmail, UUID idTarefa);
  
}
