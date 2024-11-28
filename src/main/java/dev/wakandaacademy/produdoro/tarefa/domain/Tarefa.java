package dev.wakandaacademy.produdoro.tarefa.domain;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Document(collection = "Tarefa")
public class Tarefa {
	@Id
	private UUID idTarefa;
	@NotBlank
	private String descricao;
	@Indexed
	private UUID idUsuario;
	@Indexed
	private UUID idArea;
	@Indexed
	private UUID idProjeto;
	private StatusTarefa status;
	private StatusAtivacaoTarefa statusAtivacao;
	private int contagemPomodoro;
	private Integer posicao;

	public Tarefa(TarefaRequest tarefaRequest, Integer novaPosicao) {
		this.idTarefa = UUID.randomUUID();
		this.idUsuario = tarefaRequest.getIdUsuario();
		this.descricao = tarefaRequest.getDescricao();
		this.idArea = tarefaRequest.getIdArea();
		this.idProjeto = tarefaRequest.getIdProjeto();
		this.status = StatusTarefa.CONCLUIDA;
		this.statusAtivacao = StatusAtivacaoTarefa.INATIVA;
		this.contagemPomodoro = 1;
		this.posicao = novaPosicao;
	}

	public void pertenceAoUsuario(Usuario usuarioPorEmail) {
		if (!this.idUsuario.equals(usuarioPorEmail.getIdUsuario())) {
			throw APIException.build(HttpStatus.UNAUTHORIZED, "Usuário não é dono da Tarefa solicitada!");
		}
	}

  public void alteraPosicao(int novaPosicao) {
		this.posicao = novaPosicao;
    }
  
  public void edita(EditaTarefaRequest tarefaRequest) {
 		this.descricao = tarefaRequest.getDescricao();
  }

	public void incrementaPomodoro(Tarefa tarefa, Usuario usuario) {
		pertenceAoUsuario(usuario);
		if (!usuario.getStatus().equals(StatusUsuario.FOCO)) {
			ativaTarefa();
			usuario.mudaParaFoco(usuario.getIdUsuario());
		} else {
			tarefa.incrementaPomodoro();
			verificaQuantidadePomodoro(tarefa, usuario);
		}
	}

	private void incrementaPomodoro() {
		this.contagemPomodoro++;
	}

	private void verificaQuantidadePomodoro(Tarefa tarefa, Usuario usuario) {
		int totalPomodoro = tarefa.getContagemPomodoro();
		if (totalPomodoro % 4 == 0) {
			usuario.mudaStatusParaPausaLonga(usuario.getIdUsuario());
		} else {
			usuario.mudaStatusParaPausaCurta(usuario.getIdUsuario());
		}
	}

	public void validaToken(Usuario usuario) {
		if (!this.idUsuario.equals(usuario.getIdUsuario())) {
			throw APIException.build(HttpStatus.UNAUTHORIZED, "Token não corresponde ao dono da tarefa!");
		}
	}

	public void verificaSeEstaAtiva() {
		if (this.statusAtivacao.equals(StatusAtivacaoTarefa.ATIVA)) {
			throw APIException.build(HttpStatus.CONFLICT, "Tarefa já está ativa!");
		}
	}

	public void ativaTarefa() {
		this.statusAtivacao = StatusAtivacaoTarefa.ATIVA;
	}

	public void concluiTarefa() {
		this.status = StatusTarefa.CONCLUIDA;
	}
}