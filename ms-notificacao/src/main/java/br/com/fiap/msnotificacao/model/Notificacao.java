package br.com.fiap.msnotificacao.model;

import br.com.fiap.msnotificacao.model.enums.SituacaoNotificacao;
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
public class Notificacao {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name="idNotificacao", columnDefinition = "char(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    private String idConsulta;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dataNotificacao;

    private String mensagem;

    @Enumerated(EnumType.STRING)
    private SituacaoNotificacao situacao;

    @CreationTimestamp
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    private LocalDateTime dataAtualizacao;

}
