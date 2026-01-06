package br.com.fiap.agendamed.service;

import br.com.fiap.agendamed.controller.usuario.dto.UsuarioRequestDTO;
import br.com.fiap.agendamed.controller.usuario.dto.UsuarioResponseDTO;
import br.com.fiap.agendamed.model.Usuario;
import br.com.fiap.agendamed.model.enums.PerfilUsuario;
import br.com.fiap.agendamed.model.enums.SituacaoUsuario;
import br.com.fiap.agendamed.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;

    public Usuario create(Usuario usuario) {
        return  repository.save(usuario);
    }

    public Usuario update(Usuario usuario) {
        return repository.save(usuario);
    }

    public void delete(Usuario usuario) {
        repository.deleteById(usuario.getId());
    }

    public Optional<Usuario> findById(UUID id) {
        return repository.findById(id);
    }

    public Optional<Usuario> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public List<Usuario> findAll() {
        return repository.findAll();
    }

    public List<Usuario> findAllByPerfil(PerfilUsuario perfil) {
        return repository.findAllByPerfil(perfil);
    }

    public Usuario from(UsuarioRequestDTO dto) {
        Usuario novo = new Usuario();
        novo.setNome(dto.nome());
        novo.setEmail(dto.email());
        novo.setTelefone(dto.telefone());
        novo.setSenha(dto.senha());
        novo.setPerfil(PerfilUsuario.from(dto.perfil()));
        novo.setSituacao(SituacaoUsuario.from(dto.situacao()));
        return novo;
    }

    public UsuarioResponseDTO to(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId().toString(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getPerfil(),
                usuario.getSituacao());
    }
}
