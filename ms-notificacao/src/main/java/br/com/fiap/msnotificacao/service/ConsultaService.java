package br.com.fiap.msnotificacao.service;

import br.com.fiap.msnotificacao.dto.ConsultaResponseDTO;
import br.com.fiap.msnotificacao.dto.LoginRequest;
import br.com.fiap.msnotificacao.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final RestClient restClient;

    private String currentToken;
    private LocalDateTime tokenExpirationTime;

    @Value("${api.client.email}")
    private String clientEmail;

    @Value("${api.client.password}")
    private String clientPassword;

    public List<ConsultaResponseDTO> lista(String data) {
        String token = getAccessToken();
        return restClient.get()
                .uri("/consultas?data={data}", data)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(
                        new ParameterizedTypeReference<List<ConsultaResponseDTO>>() {}
                );
    }

    private String getAccessToken() {
        if (currentToken == null || isTokenExpired()) {
            authenticate();
        }
        return currentToken;
    }

    private void authenticate() {
        LoginRequest loginRequest = new LoginRequest(clientEmail, clientPassword);

        LoginResponse response = restClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginRequest)
                .retrieve()
                .body(LoginResponse.class);

        if (response != null) {
            this.currentToken = response.token();
            this.tokenExpirationTime = LocalDateTime.now().plusSeconds(response.expiresIn() - 10);
        }
    }

    private boolean isTokenExpired() {
        return LocalDateTime.now().isAfter(tokenExpirationTime);
    }
}
