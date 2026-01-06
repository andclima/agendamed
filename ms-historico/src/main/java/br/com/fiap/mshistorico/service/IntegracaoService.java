package br.com.fiap.mshistorico.service;

import br.com.fiap.mshistorico.dto.LoginRequest;
import br.com.fiap.mshistorico.dto.LoginResponse;
import br.com.fiap.mshistorico.model.Consulta;
import br.com.fiap.mshistorico.model.Usuario;
import br.com.fiap.mshistorico.model.enums.PerfilUsuario;
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
public class IntegracaoService {

    private final RestClient restClient;
    private LocalDateTime tokenExpirationTime;
    private String currentToken;

    @Value("${api.client.email}")
    private String clientEmail;

    @Value("${api.client.password}")
    private String clientPassword;

    public List<Consulta> findAllConsulta(String pacienteId, boolean apenasFutura) {
        String token = getAccessToken();
        List<Consulta> lista = null;
        if (pacienteId == null) {
            lista = restClient.get()
                    .uri("/consultas/gql")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(
                        new ParameterizedTypeReference<List<Consulta>>() {}
                    );
        } else {
            lista = restClient.get()
                    .uri("/consultas/gql?paciente={id}&futura={apenasFutura}", pacienteId, (apenasFutura ? "1": "0"))
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(
                        new ParameterizedTypeReference<List<Consulta>>() {}
                    );
        }
        return lista;
    }

    public Usuario findUsuarioById(String usuarioId) {
        String token = getAccessToken();
        Usuario usuario = null;
        if (usuarioId != null) {
            usuario = restClient.get()
                        .uri("/usuarios/{id}/gql", usuarioId)
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .body(
                            new ParameterizedTypeReference<Usuario>() {}
                        );
        }
        return usuario;
    }

    public List<Usuario> findAllUsuariosByPerfil(PerfilUsuario perfil) {
        String token = getAccessToken();
        List<Usuario> lista = null;
        if (perfil == null) {
            lista = restClient.get()
                        .uri("/usuarios/gql")
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .body(
                            new ParameterizedTypeReference<List<Usuario>>() {}
                        );
        } else {
            lista = restClient.get()
                        .uri("/usuarios/gql?perfil={pefil}", perfil)
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .body(
                            new ParameterizedTypeReference<List<Usuario>>() {}
                        );
        }
        return lista;
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
