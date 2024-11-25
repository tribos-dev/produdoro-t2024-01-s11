package dev.wakandaacademy.produdoro.tarefa.infra;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.NovaPosicaoRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@Repository
@Log4j2
@RequiredArgsConstructor
public class TarefaInfraRepository implements TarefaRepository {

    private final TarefaSpringMongoDBRepository tarefaSpringMongoDBRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public Tarefa salva(Tarefa tarefa) {
        log.info("[inicia] TarefaInfraRepository - salva");
        try {
            tarefaSpringMongoDBRepository.save(tarefa);
        } catch (DataIntegrityViolationException e) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Tarefa já cadastrada", e);
        }
        log.info("[finaliza] TarefaInfraRepository - salva");
        return tarefa;
    }
    @Override
    public Optional<Tarefa> buscaTarefaPorId(UUID idTarefa) {
        log.info("[inicia] TarefaInfraRepository - buscaTarefaPorId");
        Optional<Tarefa> tarefaPorId = tarefaSpringMongoDBRepository.findByIdTarefa(idTarefa);
        log.info("[finaliza] TarefaInfraRepository - buscaTarefaPorId");
        return tarefaPorId;
    }

    @Override
    public List<Tarefa> buscaTarefaPorIdUsuario(UUID idUsuario) {
        log.info("[inicia] TarefaRestController - buscaTarefaPorIdUsuario");
        List<Tarefa> todasAsTarefas = tarefaSpringMongoDBRepository.findAllByIdUsuario(idUsuario);
        log.info("[finaliza] TarefaRestController - buscaTarefaPorIdUsuario");
        return todasAsTarefas;
    }

    @Override
    public Integer contagemPosicao(UUID idUsuario) {
        return tarefaSpringMongoDBRepository.countByIdUsuario(idUsuario);
    }

    @Override
    public void defineNovaPosicaoTarefa(Tarefa tarefa, List<Tarefa> todasTarefas, NovaPosicaoRequest novaPosicao) {
        log.info("[inicia] TarefaRestController - defineNovaPosicaoTarefa");
        validaNovaPosicao(tarefa, todasTarefas, novaPosicao);
        int posicaoAtualTarefa = tarefa.getPosicaoTarefa();
        int novaPosicaoTarefa = novaPosicao.getNovaPosicao();
        if (novaPosicaoTarefa < posicaoAtualTarefa){
            for (int i = novaPosicaoTarefa; i < posicaoAtualTarefa; i++){
                Tarefa tarefaDeslocada = todasTarefas.get(i);
                atualizaPosicaoTarefa(tarefaDeslocada, i + 1);
            }
//            IntStream.range(novaPosicaoTarefa, posicaoAtualTarefa)
//                    .forEach(i -> atualizaPosicaoTarefa(todasTarefas.get(i), i++));
        } else if (novaPosicaoTarefa > posicaoAtualTarefa) {
            for (int i = posicaoAtualTarefa + 1; i <= novaPosicaoTarefa; i++){
                Tarefa tarefaDeslocada = todasTarefas.get(i);
                atualizaPosicaoTarefa(tarefaDeslocada, i -1);
            }
//            IntStream.range(posicaoAtualTarefa + 1, novaPosicaoTarefa + 1)
//                    .forEach(i -> atualizaPosicaoTarefa(todasTarefas.get(i), i--));
        }
        tarefa.alteraPosicaoTarefa(novaPosicaoTarefa);
        atualizaPosicaoTarefa(tarefa, novaPosicaoTarefa);
        log.info("[finaliza] TarefaRestController - defineNovaPosicaoTarefa");
    }

    private void atualizaPosicaoTarefa(Tarefa tarefa, int i) {
        Query query = new Query(Criteria.where("idTarefa").is(tarefa.getIdTarefa()));
        Update update = new Update().set("posicao", i);
        mongoTemplate.updateFirst(query, update, Tarefa.class);
    }

    private void validaNovaPosicao(Tarefa tarefa, List<Tarefa> todasTarefas, NovaPosicaoRequest novaPosicao) {
            int posicaoAntiga = tarefa.getPosicaoTarefa();
            int tamanhoListaTarefa = todasTarefas.size();
//            if (novaPosicao.getNovaPosicao() >= tamanhoListaTarefa || novaPosicao.getNovaPosicao() <= posicaoAntiga){
//                String mensagem = novaPosicao.getNovaPosicao() >= tamanhoListaTarefa
//                        ? "A posição da tarefa não pode ser maior ou igual à quantidade total de tarefas do usuário."
//                        : "A posição da tarefa é igual a posição atual da tarefa, insira nova posição";
//            }
            if (novaPosicao.getNovaPosicao() >= tamanhoListaTarefa) {
                throw APIException.build(
                        HttpStatus.NOT_FOUND,
                        "A posição da tarefa não pode ser maior ou igual à quantidade total de tarefas do usuário."
                );
            }
        if (novaPosicao.getNovaPosicao() < 0) {
            throw APIException.build(
                    HttpStatus.NOT_FOUND,
                    "A posição da tarefa deve ser maior ou igual a zero."
            );
        }
            if (novaPosicao.getNovaPosicao() <= posicaoAntiga) {
                log.info("A nova posição é igual à posição atual. Nenhuma alteração necessária.");
            }
    }
}
