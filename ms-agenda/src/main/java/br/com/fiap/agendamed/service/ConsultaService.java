package br.com.fiap.agendamed.service;

import br.com.fiap.agendamed.controller.consulta.dto.ConsultaRequestDTO;
import br.com.fiap.agendamed.controller.consulta.dto.ConsultaResponseDTO;
import br.com.fiap.agendamed.model.Consulta;
import br.com.fiap.agendamed.model.Usuario;
import br.com.fiap.agendamed.model.enums.SituacaoConsulta;
import br.com.fiap.agendamed.repository.ConsultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final UsuarioService svcUsuario;
    private final ConsultaRepository consultaRepository;

    public Consulta create(Consulta consulta) {
        return consultaRepository.save(consulta);
    }

    public Consulta update(Consulta consulta) {
        return consultaRepository.save(consulta);
    }

    public void delete(Consulta consulta) {
        consultaRepository.deleteById(consulta.getId());
    }

    public Optional<Consulta> findById(UUID id) {
        return consultaRepository.findById(id);
    }

    public Optional<Consulta> findByMedicoAndDataConsulta(Usuario medico, LocalDateTime dataConsulta) {
        return consultaRepository.findByMedicoAndDataConsulta(medico, dataConsulta);
    }

    public List<Consulta> findAll() {
        return consultaRepository.findAll();
    }

    public List<Consulta> findAllByPaciente(Usuario paciente) {
        return consultaRepository.findAllByPaciente(paciente);
    }

    public List<Consulta> findAllByDataConsultaBetweenAndSituacao(LocalDateTime dataInicio, LocalDateTime dataFim, SituacaoConsulta situacao) {
        return consultaRepository.findAllByDataConsultaBetweenAndSituacao(dataInicio, dataFim, situacao);
    }

    public List<Consulta> findAllByPacienteAndDataConsultaAfter(Usuario paciente, LocalDateTime dataConsulta) {
        return consultaRepository.findAllByPacienteAndDataConsultaAfter(paciente, dataConsulta);
    }

    public Consulta from(ConsultaRequestDTO dto) {
        Consulta novo = new Consulta();
        novo.setDataConsulta(dto.dataConsulta());
        novo.setAnotacao(dto.anotacao());

        Optional<Usuario> medico = svcUsuario.findById(UUID.fromString(dto.medicoId()));
        Optional<Usuario> paciente = svcUsuario.findById(UUID.fromString(dto.pacienteId()));

        if (medico.isPresent()) novo.setMedico(medico.get());
        if (paciente.isPresent()) novo.setPaciente(paciente.get());

        return novo;
    }

    public ConsultaResponseDTO to(Consulta consulta) {
        ConsultaResponseDTO dto = new ConsultaResponseDTO(
                consulta.getId().toString(),
                consulta.getMedico().getId().toString(),
                consulta.getMedico().getNome(),
                consulta.getPaciente().getId().toString(),
                consulta.getPaciente().getNome(),
                consulta.getPaciente().getEmail(),
                consulta.getUsuario().getId().toString(),
                consulta.getUsuario().getNome(),
                consulta.getDataConsulta(),
                consulta.getAnotacao(),
                consulta.getDataCriacao(),
                consulta.getDataAtualizacao(),
                consulta.getSituacao());
        return dto;
    }
}
