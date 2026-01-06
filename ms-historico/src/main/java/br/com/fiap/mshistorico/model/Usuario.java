package br.com.fiap.mshistorico.model;

import br.com.fiap.mshistorico.model.enums.PerfilUsuario;
import br.com.fiap.mshistorico.model.enums.SituacaoUsuario;
import lombok.Data;

import java.util.UUID;

@Data
public class Usuario {
    private UUID id;
    private String nome;
    private String email;
    private String telefone;
    private String senha;
    private PerfilUsuario perfil;
    private SituacaoUsuario situacao;
}
