package br.com.fiap.agendamed.controller.consulta;

import br.com.fiap.agendamed.config.ConsultaCreatedEvent;
import br.com.fiap.agendamed.controller.consulta.dto.ConsultaRequestDTO;
import br.com.fiap.agendamed.controller.consulta.dto.ConsultaResponseDTO;
import br.com.fiap.agendamed.controller.consulta.exception.ConsultaMedicoIndisponivelException;
import br.com.fiap.agendamed.controller.consulta.exception.ConsultaNaoEncontradaException;
import br.com.fiap.agendamed.controller.usuario.exception.UsuarioNaoEncontradoException;
import br.com.fiap.agendamed.model.Consulta;
import br.com.fiap.agendamed.model.Usuario;
import br.com.fiap.agendamed.model.enums.PerfilUsuario;
import br.com.fiap.agendamed.model.enums.SituacaoConsulta;
import br.com.fiap.agendamed.service.ConsultaService;
import br.com.fiap.agendamed.service.NotificacaoService;
import br.com.fiap.agendamed.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("consultas")
@RequiredArgsConstructor
public class ConsultaController {

    private final ConsultaService svcConsulta;
    private final UsuarioService svcUsuario;
    private final NotificacaoService svcNotificacao;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MEDICO', 'SCOPE_ENFERMEIRO', 'SCOPE_PACIENTE')")
    public ResponseEntity<List<ConsultaResponseDTO>> list(
            @RequestParam(value = "data", required = false) String data,
            JwtAuthenticationToken token
    ) {
        List<ConsultaResponseDTO> lista = new ArrayList<>();

        Optional<Usuario> usuario = svcUsuario.findById(UUID.fromString(token.getName()));
        Usuario paciente;
        if (usuario.get().getPerfil().equals(PerfilUsuario.PACIENTE)) {
            paciente = usuario.get();
        } else {
            paciente = null;
        }

        if (data == null || data.isEmpty()) {
            if (paciente != null) {
                svcConsulta.findAll()
                        .stream()
                        .filter(consulta -> consulta.getPaciente().getId().equals(paciente.getId()))
                        .forEach(consulta -> lista.add(svcConsulta.to(consulta)));
            } else {
                svcConsulta.findAll()
                        .stream()
                        .forEach(consulta -> lista.add(svcConsulta.to(consulta)));
            }
            return ResponseEntity.status(HttpStatus.OK).body(lista);
        }

        try {
            LocalDate d = LocalDate.parse(data, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDateTime dataInicio = d.atStartOfDay();
            LocalDateTime dataFim = d.plusDays(1).atStartOfDay().minusNanos(1);
            if (paciente != null) {
                svcConsulta.findAllByDataConsultaBetweenAndSituacao(dataInicio, dataFim, SituacaoConsulta.AGENDADA)
                        .stream()
                        .filter(consulta -> consulta.getPaciente().getId().equals(paciente.getId()))
                        .forEach(consulta -> lista.add(svcConsulta.to(consulta)));
            } else {
                svcConsulta.findAllByDataConsultaBetweenAndSituacao(dataInicio, dataFim, SituacaoConsulta.AGENDADA)
                        .stream()
                        .forEach(consulta -> lista.add(svcConsulta.to(consulta)));
            }
            return ResponseEntity.status(HttpStatus.OK).body(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(lista);
        }
    }

    @GetMapping("/gql")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<Consulta>> listGraphQL(
            @RequestParam(value = "paciente", required = false) String pacienteId,
            @RequestParam(value = "futura", required = false, defaultValue = "0") String futura
    ) {
        List<Consulta> lista = new ArrayList<>();
        if (pacienteId != null) {
            Usuario paciente = svcUsuario.findById(UUID.fromString(pacienteId)).orElse(null);
            if (futura != null && futura.equals("1")) {
                lista = svcConsulta.findAllByPacienteAndDataConsultaAfter(paciente, LocalDateTime.now());
            } else {
                lista = svcConsulta.findAllByPaciente(paciente);
            }
        } else {
            lista = svcConsulta.findAll();
        }
        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_MEDICO', 'SCOPE_ENFERMEIRO', 'SCOPE_PACIENTE')")
    public ResponseEntity<ConsultaResponseDTO> findById(@PathVariable("id") String uuid, JwtAuthenticationToken token) {
        Optional<Consulta> encontrado = svcConsulta.findById(UUID.fromString(uuid));
        if (encontrado.isEmpty()) {
            throw new RuntimeException("Consulta não encontrada");
        }

        Optional<Usuario> usuario = svcUsuario.findById(UUID.fromString(token.getName()));
        if (usuario.get().getPerfil().equals(PerfilUsuario.PACIENTE) && !encontrado.get().getPaciente().getId().equals(usuario.get().getId())) {
            throw new AccessDeniedException("Acesso não autorizado");
        }
        ConsultaResponseDTO response = svcConsulta.to(encontrado.get());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_MEDICO', 'SCOPE_ENFERMEIRO')")
    public ResponseEntity<ConsultaResponseDTO> create(@Valid @RequestBody ConsultaRequestDTO dto, JwtAuthenticationToken token) {
        Optional<Usuario> medico = svcUsuario.findById(UUID.fromString(dto.medicoId()));
        if (medico.isEmpty() || !medico.get().getPerfil().equals(PerfilUsuario.MEDICO)) {
            throw new UsuarioNaoEncontradoException("Médico não encontrado");
        }
        Optional<Usuario> paciente = svcUsuario.findById(UUID.fromString(dto.pacienteId()));
        if (paciente.isEmpty() || !paciente.get().getPerfil().equals(PerfilUsuario.PACIENTE)) {
            throw new UsuarioNaoEncontradoException("Paciente não encontrado");
        }
        Optional<Usuario> usuario = svcUsuario.findById(UUID.fromString(token.getName()));  // Pega pelo token
        if (usuario.isEmpty()) {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado");
        }

        Optional<Consulta> atual = svcConsulta.findByMedicoAndDataConsulta(medico.get(), dto.dataConsulta());
        if (atual.isPresent()) {
            throw new ConsultaMedicoIndisponivelException("Este médico já possui uma consulta para esta data e horário");
        }

        Consulta nova = new Consulta();
        nova.setMedico(medico.get());
        nova.setPaciente(paciente.get());
        nova.setUsuario(usuario.get());
        nova.setDataConsulta(dto.dataConsulta());
        nova.setAnotacao(dto.anotacao());
        nova.setSituacao(SituacaoConsulta.AGENDADA);
        Consulta salva = svcConsulta.create(nova);

        ConsultaCreatedEvent event = new ConsultaCreatedEvent(
                salva.getId().toString(),
                salva.getPaciente().getId().toString(),
                salva.getMedico().getId().toString(),
                salva.getPaciente().getNome(),
                salva.getMedico().getNome(),
                salva.getPaciente().getEmail(),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(salva.getDataConsulta())
        );
        svcNotificacao.enviar(event);

        ConsultaResponseDTO response = svcConsulta.to(salva);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_MEDICO', 'SCOPE_ENFERMEIRO')")
    public ResponseEntity<ConsultaResponseDTO> update(@PathVariable("id") String uuid,
                                                      @Valid @RequestBody ConsultaRequestDTO dto,
                                                      JwtAuthenticationToken token) {
        Optional<Consulta> encontrada = svcConsulta.findById(UUID.fromString(uuid));
        if (encontrada.isEmpty()) {
            throw new ConsultaNaoEncontradaException("Consulta não encontrada");
        }

        Optional<Consulta> existente = svcConsulta.findByMedicoAndDataConsulta(encontrada.get().getMedico(), dto.dataConsulta());
        if (existente.isPresent()) {
            if (!existente.get().getId().equals(encontrada.get().getId())) {
                throw new ConsultaMedicoIndisponivelException("Este médico já possui uma consulta para esta data e horário");
            }
        }

        Optional<Usuario> usuario = svcUsuario.findById(UUID.fromString(token.getName()));  // Pega pelo token
        Usuario medico;
        if (usuario.get().getPerfil().equals(PerfilUsuario.MEDICO)) {
            medico = usuario.get();
        } else {
            medico = null;
        }

        if (dto.situacao().toUpperCase().equals(SituacaoConsulta.REALIZADA.name()) && medico == null) {
            throw new AccessDeniedException("Usuário não possui permissão para encerrar a consulta");
        }

        Consulta registro = encontrada.get();
        registro.setDataConsulta(dto.dataConsulta());
        registro.setSituacao(SituacaoConsulta.from(dto.situacao().toUpperCase()));
        if (medico != null) {
            registro.setAnotacao(dto.anotacao());
        }
        Consulta salva = svcConsulta.update(registro);

        ConsultaCreatedEvent event = new ConsultaCreatedEvent(
                salva.getId().toString(),
                salva.getPaciente().toString(),
                salva.getMedico().toString(),
                salva.getPaciente().getNome(),
                salva.getMedico().getNome(),
                salva.getPaciente().getEmail(),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(salva.getDataConsulta())
        );
        svcNotificacao.enviar(event);

        ConsultaResponseDTO response = svcConsulta.to(salva);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_MEDICO', 'SCOPE_ENFERMEIRO')")
    public ResponseEntity<Void> deleteById(@PathVariable("id") String uuid) {
        Optional<Consulta> encontrado = svcConsulta.findById(UUID.fromString(uuid));
        if (encontrado.isEmpty()) {
            throw new ConsultaNaoEncontradaException("Consulta não encontrada");
        }
        try {
            svcConsulta.delete(encontrado.get());
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível excluir a consulta");
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
