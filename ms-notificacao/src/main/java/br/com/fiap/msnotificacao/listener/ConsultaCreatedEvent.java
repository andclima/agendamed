package br.com.fiap.msnotificacao.listener;

import java.time.LocalDateTime;

public record ConsultaCreatedEvent(
        String consultaId,
        String pacienteId,
        String medicoId,
        String nomePaciente,
        String nomeMedico,
        String emailPaciente,
        String dataHora
) {
}
