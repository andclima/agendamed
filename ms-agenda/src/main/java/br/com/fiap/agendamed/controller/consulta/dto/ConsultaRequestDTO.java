package br.com.fiap.agendamed.controller.consulta.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ConsultaRequestDTO(
        @NotBlank(message =  "Id do médico é obrigatório")
        String medicoId,

        @NotBlank(message =  "Id do paciente é obrigatório")
        String pacienteId,

        @NotNull
        @FutureOrPresent
        LocalDateTime dataConsulta,

        String anotacao,
        String situacao
) {
}
