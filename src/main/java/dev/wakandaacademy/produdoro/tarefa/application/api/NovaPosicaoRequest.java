package dev.wakandaacademy.produdoro.tarefa.application.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.PositiveOrZero;

@Value
public class NovaPosicaoRequest {
    @PositiveOrZero(message = "Posição deve ser maior ou igual a zero.")
    private Integer novaPosicao;

    @JsonCreator
    public NovaPosicaoRequest(@JsonProperty("novaPosicao") Integer novaPosicao){
        this.novaPosicao = novaPosicao;
    }
}
