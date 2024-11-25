package dev.wakandaacademy.produdoro.tarefa.application.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.PositiveOrZero;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NovaPosicaoRequest {
    @PositiveOrZero(message = "Posição deve ser maior ou igual a zero.")
    private int novaPosicao;
}
