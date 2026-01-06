package br.com.fiap.agendamed.model;

import br.com.fiap.agendamed.model.enums.SituacaoConsulta;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Consulta {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name="idConsulta", columnDefinition = "char(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "idMedico")
    private Usuario medico;

    @ManyToOne
    @JoinColumn(name = "idPaciente")
    private Usuario paciente;

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dataConsulta;

    @CreationTimestamp
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    private LocalDateTime dataAtualizacao;

    private String anotacao;

    @Enumerated(EnumType.STRING)
    private SituacaoConsulta situacao;

    public Consulta() {
        this.situacao = SituacaoConsulta.AGENDADA;
        this.anotacao = "";
    }
}
