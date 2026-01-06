package br.com.fiap.agendamed.controller.usuario.dto;

import br.com.fiap.agendamed.model.enums.PerfilUsuario;
import br.com.fiap.agendamed.model.enums.SituacaoUsuario;

public record UsuarioResponseDTO(
        String uuid,
        String nome,
        String email,
        String telefone,
        PerfilUsuario perfil,
        SituacaoUsuario situacao
) {
}
