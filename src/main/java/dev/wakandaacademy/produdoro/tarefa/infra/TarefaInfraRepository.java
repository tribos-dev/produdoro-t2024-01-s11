package dev.wakandaacademy.produdoro.tarefa.infra;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;

import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;

import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;

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

    public void inativaTarefa(UUID idUsuario) {
        log.info("[inicia] TarefaInfraRepository - inativaTarefa");
        Query query = new Query(Criteria.where("idUsuario").is(idUsuario)
                .and("statusAtivacao").is(StatusAtivacaoTarefa.ATIVA));
        Update update = new Update().set("statusAtivacao", StatusAtivacaoTarefa.INATIVA);
        mongoTemplate.updateMulti(query, update, Tarefa.class);
        log.info("[finaliza] TarefaInfraRepository - inativaTarefa");
    }

    public List<Tarefa> buscaTarefaPorIdUsuario(UUID idUsuario) {
        log.info("[inicia] TarefaRestController - buscaTarefaPorIdUsuario");
        List<Tarefa> todasAsTarefas = tarefaSpringMongoDBRepository.findAllByIdUsuario(idUsuario);
        log.info("[finaliza] TarefaRestController - buscaTarefaPorIdUsuario");
        return todasAsTarefas;
    }

    @Override
    public void deletaTodasAsTarefas(List<Tarefa> tarefas) {
        log.info("[inicia] TarefaInfraRepository - deletaTodasAsTarefas");
        tarefaSpringMongoDBRepository.deleteAll(tarefas);
        log.info("[finaliza] TarefaInfraRepository - deletaTodasAsTarefas");
    }
    
    @Override
    public List<Tarefa> buscaTarefasConcluidas(UUID idUsuario) {
        log.info("[inicia] TarefaRestController - buscaTarefasConcluidas");
        List<Tarefa> tarefasConcluidas = tarefaSpringMongoDBRepository.findAllByIdUsuarioAndStatus(idUsuario, StatusTarefa.CONCLUIDA);
        log.info("[finaliza] TarefaRestController - buscaTarefasConcluidas");
        return tarefasConcluidas;
    }

    @Override
    public void deletaTarefasConcluidas(List<Tarefa> tarefasConcluidas) {
        log.info("[inicia] TarefaRestController - deletaTarefasConcluidas");
        tarefaSpringMongoDBRepository.deleteAll(tarefasConcluidas);
        log.info("[finaliza] TarefaRestController - deletaTarefasConcluidas");
    }
}
