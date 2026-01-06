package br.com.fiap.agendamed.repository;

import br.com.fiap.agendamed.model.Usuario;
import br.com.fiap.agendamed.model.enums.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findAllByPerfil(PerfilUsuario perfil);
}
