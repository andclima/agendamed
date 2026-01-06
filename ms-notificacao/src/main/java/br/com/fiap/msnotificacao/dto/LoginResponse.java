package br.com.fiap.msnotificacao.dto;

public record LoginResponse(
        String token,
        Long expiresIn
) {
}
