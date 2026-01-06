package br.com.fiap.agendamed.repository;

import br.com.fiap.agendamed.model.Consulta;
import br.com.fiap.agendamed.model.Usuario;
import br.com.fiap.agendamed.model.enums.SituacaoConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, UUID> {

    List<Consulta> findAllByPaciente(Usuario paciente);
    List<Consulta> findAllByMedico(Usuario medico);
    Optional<Consulta> findByMedicoAndDataConsulta(Usuario medico, LocalDateTime dataConsulta);
    List<Consulta> findAllByDataConsultaBetweenAndSituacao(LocalDateTime dataInicio, LocalDateTime dataFim, SituacaoConsulta situacaoConsulta);
    List<Consulta> findAllByPacienteAndDataConsultaAfter(Usuario paciente, LocalDateTime dataConsulta);
}
