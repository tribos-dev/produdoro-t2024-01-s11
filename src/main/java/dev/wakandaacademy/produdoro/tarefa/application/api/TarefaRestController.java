package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.wakandaacademy.produdoro.config.security.service.TokenService;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.service.TarefaService;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@RequiredArgsConstructor
public class TarefaRestController implements TarefaAPI {
	private final TarefaService tarefaService;
	private final TokenService tokenService;

	public TarefaIdResponse postNovaTarefa(TarefaRequest tarefaRequest) {
		log.info("[inicia]  TarefaRestController - postNovaTarefa");
		TarefaIdResponse tarefaCriada = tarefaService.criaNovaTarefa(tarefaRequest);
		log.info("[finaliza]  TarefaRestController - postNovaTarefa");
		return tarefaCriada;
	}

	@Override
	public TarefaDetalhadoResponse detalhaTarefa(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - detalhaTarefa");
		String usuario = getUsuarioByToken(token);
		Tarefa tarefa = tarefaService.detalhaTarefa(usuario,idTarefa);
		log.info("[finaliza] TarefaRestController - detalhaTarefa");
		return new TarefaDetalhadoResponse(tarefa);
	}

	@Override
	public void editaTarefa(String token, UUID idTarefa, EditaTarefaRequest tarefaRequest) {
		log.info("[inicia] TarefaRestController - editaTarefa");
		String emailUsuario = getUsuarioByToken(token);
		tarefaService.editaTarefa(emailUsuario, idTarefa, tarefaRequest);
		log.info("[finaliza] TarefaRestController - editaTarefa");
  }
  
	public void ativaTarefa(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - ativaTarefa");
		String email = getUsuarioByToken(token);
		tarefaService.ativaTarefa(email, idTarefa);
		log.info("[finaliza] TarefaRestController - ativaTarefa");
	}

	public void concluiTarefa(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - concluiTarefa");
		String usuarioEmail = getUsuarioByToken(token);
		tarefaService.concluiTarefa(usuarioEmail, idTarefa);
		log.info("[finaliza] TarefaRestController - concluiTarefa");
	}
	
	public List<TarefaListResponse> listarTodasAsTarefas(String token, UUID idUsuario) {
		log.info("[inicia] TarefaRestController - listarTodasAsTarefas");
		String usuario = getUsuarioByToken(token);
		List<TarefaListResponse> tarefas = tarefaService.buscarTodasAsTarefas(usuario, idUsuario);
		log.info("[finaliza] TarefaRestController - listarTodasAsTarefas");
		return tarefas;
	}

	@Override
	public void alteraPosicaoTarefa(String token, UUID idTarefa, NovaPosicaoRequest novaPosicao) {
		log.info("[inicia] TarefaRestController - alteraPosicaoTarefa");
		String usuario = getUsuarioByToken(token);
		tarefaService.alteraPosicaoTarefa(usuario, idTarefa, novaPosicao);
		log.info("[finaliza] TarefaRestController - alteraPosicaoTarefa");
  }
  
  @Override
  public void deletaTodasAsTarefas(String token, UUID idUsuario) {
		log.info("[inicia] TarefaRestController - deletaTodasAsTarefas");
		String usuario = getUsuarioByToken(token);
		tarefaService.deletaTodasAsTarefas(usuario, idUsuario);
		log.info("[finaliza] TarefaRestController - deletaTodasAsTarefas");
	}

	@Override
	public void deletaTarefasConcluidas(String token, UUID idUsuario) {
		log.info("[inicia] TarefaRestController - deletaTarefasConcluidas");
		String usuarioEmail = getUsuarioByToken(token);
		tarefaService.deletaTarefasConcluidas(usuarioEmail, idUsuario);
		log.info("[finaliza] TarefaRestController - deletaTarefasConcluidas");
	}

	private String getUsuarioByToken(String token) {
		log.debug("[token] {}", token);
		String usuario = tokenService.getUsuarioByBearerToken(token).orElseThrow(() -> APIException.build(HttpStatus.UNAUTHORIZED, token));
		log.info("[usuario] {}", usuario);
		return usuario;
	}

	@Override
	public void incrementaPomodoro(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - incrementaPomodoro");
		String usuarioEmail = getUsuarioByToken(token);
		tarefaService.incrementaPomodoro(usuarioEmail, idTarefa);
		log.info("[finaliza] TarefaRestController - incrementaPomodoro");
	}

}
