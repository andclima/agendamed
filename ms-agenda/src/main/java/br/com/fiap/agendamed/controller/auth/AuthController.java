package br.com.fiap.agendamed.controller.auth;

import br.com.fiap.agendamed.controller.auth.dto.LoginRequestDTO;
import br.com.fiap.agendamed.controller.auth.dto.LoginResponseDTO;
import br.com.fiap.agendamed.controller.auth.exception.CredenciaisInvalidasException;
import br.com.fiap.agendamed.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final JwtEncoder jwtEncoder;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {

        var user = usuarioService.findByEmail(loginRequest.email());
        if (user.isEmpty() || !user.get().passwordMatch(loginRequest.password(), passwordEncoder)) {
            throw new CredenciaisInvalidasException("Usuário ou senha inválida!");
        }

        var now = Instant.now();
        var expiresIn = 300L;
        var scopes = user.get().getPerfil().name();

        var claims = JwtClaimsSet.builder()
                .issuer("br.com.fiap.agendamed.msauth")
                .subject(user.get().getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scope", scopes)
                .build();

        var token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(new LoginResponseDTO(token, expiresIn));
    }

}
