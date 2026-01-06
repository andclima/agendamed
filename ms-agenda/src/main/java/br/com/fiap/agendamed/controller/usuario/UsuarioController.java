package br.com.fiap.agendamed.controller.usuario;

import br.com.fiap.agendamed.controller.exception.RequisicaoDadosInvalidosException;
import br.com.fiap.agendamed.controller.usuario.dto.UsuarioRequestDTO;
import br.com.fiap.agendamed.controller.usuario.dto.UsuarioResponseDTO;
import br.com.fiap.agendamed.controller.usuario.exception.EmailJaExisteException;
import br.com.fiap.agendamed.controller.usuario.exception.UsuarioNaoEncontradoException;
import br.com.fiap.agendamed.model.Consulta;
import br.com.fiap.agendamed.model.Usuario;
import br.com.fiap.agendamed.model.enums.PerfilUsuario;
import br.com.fiap.agendamed.model.enums.SituacaoUsuario;
import br.com.fiap.agendamed.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService svc;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<UsuarioResponseDTO>> list(@RequestParam(value = "perfil", required = false) String perfil) {
        List<UsuarioResponseDTO> list = new ArrayList<>();

        if (perfil != null) {
            try {
                PerfilUsuario perfilUsuario = PerfilUsuario.valueOf(perfil.toUpperCase());
                svc.findAllByPerfil(perfilUsuario).forEach(usuario -> {
                    if (usuario.getSituacao() == SituacaoUsuario.ATIVO) {
                        list.add(svc.to(usuario));
                    }
                });
                return ResponseEntity.status(HttpStatus.OK).body(list);
            } catch (IllegalArgumentException e) {
                throw new RequisicaoDadosInvalidosException("Perfil de usuário inválido");
            }
        }

        svc.findAll().forEach(usuario -> {
            list.add(svc.to(usuario));
        });

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/gql")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<Usuario>> listGraphQL(
            @RequestParam(value = "perfil", required = false) PerfilUsuario perfil
    ) {
        List<Usuario> lista;
        if (perfil != null) {
            lista = svc.findAllByPerfil(perfil);
        } else {
            lista = svc.findAll();
        }
        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable("id") String uuid) {
        Optional<Usuario> usuario = svc.findById(UUID.fromString(uuid));
        if (usuario.isEmpty()) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado");
        }
        UsuarioResponseDTO response = svc.to(usuario.get());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}/gql")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Usuario> findByIdGraphQL(@PathVariable("id") String uuid) {
        Optional<Usuario> usuario = svc.findById(UUID.fromString(uuid));
        Usuario response = null;
        if (usuario.isPresent()) {
            response = usuario.get();
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UsuarioRequestDTO requestDTO) {
        Optional<Usuario> usuario = svc.findByEmail(requestDTO.email());
        if (usuario.isPresent()) {
            throw new EmailJaExisteException("Já existe usuário com o e-mail informado");
        }
        Usuario novo = svc.from(requestDTO);
        novo.setSenha(passwordEncoder.encode(requestDTO.senha()));
        Usuario salvo = svc.create(novo);
        UsuarioResponseDTO response = svc.to(salvo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> update(@PathVariable("id") String uuid, @Valid @RequestBody UsuarioRequestDTO dto) {
        Optional<Usuario> encontrado = svc.findById(UUID.fromString(uuid));
        if (encontrado.isEmpty()) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado");
        }

        Optional<Usuario> usuarioEmail = svc.findByEmail(dto.email());
        if (usuarioEmail.isPresent() && !usuarioEmail.get().getId().equals(encontrado.get().getId())) {
            throw new EmailJaExisteException("E-mail já utilizado por outro usuário");
        }

        Usuario novo = encontrado.get();
        novo.setNome(dto.nome());
        novo.setEmail(dto.email());
        novo.setTelefone(dto.telefone());
        novo.setSenha(passwordEncoder.encode(dto.senha()));
        novo.setPerfil(PerfilUsuario.from(dto.perfil()));
        novo.setSituacao(SituacaoUsuario.from(dto.situacao()));
        Usuario salvo = svc.update(novo);

        UsuarioResponseDTO response = svc.to(salvo);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable("id") String uuid) {
        Optional<Usuario> usuario = svc.findById(UUID.fromString(uuid));
        if (usuario.isEmpty()) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado");
        }
        try {
            svc.delete(usuario.get());
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível excluir o usuário");
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
