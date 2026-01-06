package br.com.fiap.agendamed.controller.auth.dto;

public record LoginResponseDTO(
        String token,
        Long expiresIn
) {
}
