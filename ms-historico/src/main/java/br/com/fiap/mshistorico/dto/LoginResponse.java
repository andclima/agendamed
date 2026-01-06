package br.com.fiap.mshistorico.dto;

public record LoginResponse(
        String token,
        Long expiresIn
) {
}
