package br.com.fiap.agendamed.model;

import br.com.fiap.agendamed.model.enums.PerfilUsuario;
import br.com.fiap.agendamed.model.enums.SituacaoUsuario;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Entity
@Data
public class Usuario {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name="idUsuario", columnDefinition = "char(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;
    private String nome;
    private String email;

    private String telefone;

    private String senha;

    @Enumerated(EnumType.STRING)
    private PerfilUsuario perfil;

    @Enumerated(EnumType.STRING)
    private SituacaoUsuario situacao;

    public Usuario() {
        this.situacao = SituacaoUsuario.ATIVO;
    }

    public boolean passwordMatch(String password, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(password, this.senha);
    }

    public Object getUserId() {
        return null;
    }
}
