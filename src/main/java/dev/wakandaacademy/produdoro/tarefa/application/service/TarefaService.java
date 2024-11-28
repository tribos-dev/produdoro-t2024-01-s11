package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.tarefa.application.api.NovaPosicaoRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

import java.util.List;
import java.util.UUID;

public interface TarefaService {

  List<TarefaListResponse> buscarTodasAsTarefas(String usuario, UUID idUsuario);
  void alteraPosicaoTarefa(String usuario, UUID idTarefa, NovaPosicaoRequest novaPosicao);
  TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest);
  void editaTarefa(String emailUsuario, UUID idTarefa, EditaTarefaRequest tarefaRequest);
	Tarefa detalhaTarefa(String usuario, UUID idTarefa);
	void incrementaPomodoro(String usuarioEmail, UUID idTarefa);
	void ativaTarefa(String email, UUID idTarefa);
  void deletaTodasAsTarefas(String token, UUID idUsuario);
	void deletaTarefasConcluidas(String usuarioEmail, UUID idUsuario);
	void concluiTarefa(String usuarioEmail, UUID idTarefa);
}
