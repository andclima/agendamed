package br.com.fiap.msnotificacao.dto;


import br.com.fiap.msnotificacao.model.enums.SituacaoConsulta;

import java.time.LocalDateTime;

public record ConsultaResponseDTO(
        String uuid,
        String medicoId,
        String medicoNome,
        String pacienteId,
        String pacienteNome,
        String pacienteEmail,
        String usuarioId,
        String usuarioNome,
        LocalDateTime dataConsulta,
        String anotacao,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao,
        SituacaoConsulta situacao) {
}
