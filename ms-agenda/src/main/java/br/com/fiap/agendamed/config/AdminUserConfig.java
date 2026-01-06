package br.com.fiap.agendamed.config;

import br.com.fiap.agendamed.model.Usuario;
import br.com.fiap.agendamed.model.enums.PerfilUsuario;
import br.com.fiap.agendamed.model.enums.SituacaoUsuario;
import br.com.fiap.agendamed.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class AdminUserConfig implements CommandLineRunner {

    @Value("${api.client.email}")
    private String emailAdmin;

    @Value("${api.client.password}")
    private String senhaAdmin;

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final Logger log = LoggerFactory.getLogger(AdminUserConfig.class);

    @Override
    public void run(String... args) throws Exception {
        Optional<Usuario> userAdmin = usuarioService.findByEmail(emailAdmin);
        userAdmin.ifPresentOrElse(
                user -> {
                    log.info("Usuário admin já existe!");
                },
                () -> {
                    Usuario usuario = new Usuario();
                    usuario.setEmail(emailAdmin);
                    usuario.setNome("Admin");
                    usuario.setTelefone("(12) 34567-8901");
                    usuario.setSenha(passwordEncoder.encode(senhaAdmin));
                    usuario.setPerfil(PerfilUsuario.ADMIN);
                    usuario.setSituacao(SituacaoUsuario.ATIVO);
                    usuarioService.create(usuario);
                }
        );
    }
}
