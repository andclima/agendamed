package br.com.fiap.agendamed.controller.auth.dto;

public record LoginRequestDTO(
        String email,
        String password
) {
}
