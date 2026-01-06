package br.com.fiap.agendamed.controller.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioRequestDTO (

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @Email
        @NotBlank(message = "E-mail é obrigatório")
        String email,

        String telefone,

        @NotBlank(message = "Senha é obrigatória")
        String senha,

        @NotBlank(message = "Perfil é obrigatório")
        String perfil,

        String situacao
) {
}
