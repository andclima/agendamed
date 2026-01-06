package br.com.fiap.agendamed.config;

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
