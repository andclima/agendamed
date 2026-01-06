package br.com.fiap.mshistorico.controller;

import br.com.fiap.mshistorico.model.Consulta;
import br.com.fiap.mshistorico.model.Usuario;
import br.com.fiap.mshistorico.model.enums.PerfilUsuario;
import br.com.fiap.mshistorico.service.IntegracaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ConsultaController {

    private final IntegracaoService integracaoService;

    @QueryMapping
    public List<Usuario> usuarios() {
        return integracaoService.findAllUsuariosByPerfil(null);
    }

    @QueryMapping
    public List<Usuario> usuariosByPerfil(@Argument PerfilUsuario perfil) {
        return integracaoService.findAllUsuariosByPerfil(perfil);
    }

    @QueryMapping
    public Usuario usuarioById(@Argument String id) {
        return integracaoService.findUsuarioById(id);
    }

    @QueryMapping
    public List<Consulta> consultas() {
        return integracaoService.findAllConsulta(null, false);
    }

    @QueryMapping
    public List<Consulta> consultasByPacienteId(@Argument String id, @Argument Boolean apenasFutura) {
        try {
            UUID.fromString(id);
            return integracaoService.findAllConsulta(id, (apenasFutura != null && apenasFutura));
        } catch (IllegalArgumentException e) {
            return new ArrayList<>();
        }
    }
}