package dev.wakandaacademy.produdoro.tarefa.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class TarefaApplicationService implements TarefaService {
	private final TarefaRepository tarefaRepository;
	private final UsuarioRepository usuarioRepository;

	@Override
	public TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest) {
		log.info("[inicia] TarefaApplicationService - criaNovaTarefa");
		Tarefa tarefaCriada = tarefaRepository.salva(new Tarefa(tarefaRequest));
		log.info("[finaliza] TarefaApplicationService - criaNovaTarefa");
		return TarefaIdResponse.builder().idTarefa(tarefaCriada.getIdTarefa()).build();
	}

	@Override
	public Tarefa detalhaTarefa(String usuario, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - detalhaTarefa");
		Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
		log.info("[usuarioPorEmail] {}", usuarioPorEmail);
		Tarefa tarefa = tarefaRepository.buscaTarefaPorId(idTarefa)
				.orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Tarefa não encontrada!"));
		tarefa.pertenceAoUsuario(usuarioPorEmail);
		log.info("[finaliza] TarefaApplicationService - detalhaTarefa");
		return tarefa;
	}

	@Override
	public void incrementaPomodoro(String usuarioEmail, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - incrementaPomodoro");
		Tarefa tarefa = detalhaTarefa(usuarioEmail, idTarefa);
		Usuario usuario = usuarioRepository.buscaUsuarioPorEmail(usuarioEmail);
		tarefa.incrementaPomodoro(tarefa, usuario);
		usuarioRepository.salva(usuario);
		tarefaRepository.salva(tarefa);
		log.info("[finaliza] TarefaApplicationService - incrementaPomodoro");
	}

	@Override
	public void ativaTarefa(String email, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - ativaTarefa");
		Tarefa tarefa = tarefaRepository.buscaTarefaPorId(idTarefa)
				.orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Id da tarefa inválido!"));
		Usuario usuario = usuarioRepository.buscaUsuarioPorEmail(email);
		log.info("[usuario] {}", usuario);
		tarefa.validaToken(usuario);
		tarefa.verificaSeEstaAtiva();
		tarefaRepository.inativaTarefa(usuario.getIdUsuario());
		tarefa.ativaTarefa();
		tarefaRepository.salva(tarefa);
		log.info("[finaliza] TarefaApplicationService - ativaTarefa");
	}

	@Override
	public void concluiTarefa(String usuarioEmail, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - concluiTarefa");
		Tarefa tarefa = detalhaTarefa(usuarioEmail, idTarefa);
		tarefa.concluiTarefa();
		tarefaRepository.salva(tarefa);
		log.info("[finaliza] TarefaApplicationService - concluiTarefa");
	}

	public List<TarefaListResponse> buscarTodasAsTarefas(String usuario, UUID idUsuario) {
		log.info("[inicia] TarefaRestController - buscarTodasAsTarefas");
		Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
		usuarioRepository.buscaUsuarioPorId(idUsuario);
		usuarioPorEmail.validaUsuario(idUsuario);
		List<Tarefa> tarefas = tarefaRepository.buscaTarefaPorIdUsuario(idUsuario);
		log.info("[finaliza] TarefaRestController - buscarTodasAsTarefas");
		return TarefaListResponse.converter(tarefas);
	}

	@Override
	public void deletarTodasAsTarefas(String usuarioEmail, UUID idUsuario) {
		log.info("[inicia] TarefaApplicationService - deletarTodasAsTarefas");
		Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuarioEmail);
		Usuario usuario = usuarioRepository.buscaUsuarioPorId(idUsuario);
		usuario.validacaoUsuario(usuarioPorEmail);
		List<Tarefa> tarefas = tarefaRepository.buscaTarefaPorIdUsuario(usuario.getIdUsuario());
		if (tarefas.isEmpty()) {
			throw APIException.build(HttpStatus.CONFLICT, "Usuário não possui tarefa(as) cadastrada(as)");
		}
		tarefaRepository.deletaTodasAsTarefas(tarefas);
		log.info("[finaliza] TarefaApplicationService - deletarTodasAsTarefas");
	}

	@Override
	public void deletaTarefasConcluidas(String usuarioEmail, UUID idUsuario) {
		log.info("[inicia] TarefaRestController - deletaTarefasConcluidas");
		validaUsuario(usuarioEmail, idUsuario);
		List<Tarefa> tarefasConcluidas = tarefaRepository.buscaTarefasConcluidas(idUsuario);
		verificaSeTemTarefasConcluidas(tarefasConcluidas);
		tarefaRepository.deletaTarefasConcluidas(tarefasConcluidas);
		log.info("[finaliza] TarefaRestController - deletaTarefasConcluidas");
	}

	private void validaUsuario(String usuarioEmail, UUID idUsuario) {
		Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuarioEmail);
		Usuario usuario = usuarioRepository.buscaUsuarioPorId(idUsuario);
		usuario.validaUsuario(usuarioPorEmail.getIdUsuario());
	}

	private void verificaSeTemTarefasConcluidas(List<Tarefa> tarefasConcluidas) {
		if (tarefasConcluidas.isEmpty()) {
			throw APIException.build(HttpStatus.NOT_FOUND, "O usuário não possui tarefas concluídas");
		}
	}
}
