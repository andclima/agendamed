package br.com.fiap.mshistorico.model;

import br.com.fiap.mshistorico.model.enums.SituacaoConsulta;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Consulta {
    private UUID id;
    private Usuario medico;
    private Usuario paciente;
    private Usuario usuario;
    private LocalDateTime dataConsulta;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private String anotacao;
    private SituacaoConsulta situacao;
}
