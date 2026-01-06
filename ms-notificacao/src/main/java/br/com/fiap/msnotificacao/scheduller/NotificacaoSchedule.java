package br.com.fiap.msnotificacao.scheduller;

import br.com.fiap.msnotificacao.dto.ConsultaResponseDTO;
import br.com.fiap.msnotificacao.model.Notificacao;
import br.com.fiap.msnotificacao.model.enums.SituacaoNotificacao;
import br.com.fiap.msnotificacao.service.ConsultaService;
import br.com.fiap.msnotificacao.service.EmailService;
import br.com.fiap.msnotificacao.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
@RequiredArgsConstructor
public class NotificacaoSchedule {

    private final String CRON_NOTIFICACAO = "0 0 14 * * ?";
    private final String TEMPLATE_EMAIL_LEMBRETE = "templates/email-lembrete.html";
    private final Logger log = LoggerFactory.getLogger(NotificacaoSchedule.class);
    private final NotificacaoService notificacaoService;
    private final ConsultaService consultaService;
    private final EmailService emailService;

    @Scheduled(cron = CRON_NOTIFICACAO)
    public void runTask() {
        log.info("Executando rotina de notificacao...");
        String data = LocalDate.now().plusDays(1).atStartOfDay()
                               .format(DateTimeFormatter.ISO_DATE);
        consultaService.lista(data).forEach(this::enviarLembrete);
        log.info("Rotina de notificacao concluída!");
    }

    private void enviarLembrete(ConsultaResponseDTO consulta) {
        String nomePaciente = consulta.pacienteNome();
        nomePaciente = (nomePaciente.contains(" ") ? nomePaciente.split(" ")[0] : nomePaciente);

        String nomeMedico = consulta.medicoNome();
        nomeMedico = (nomeMedico.contains(" ") ? nomeMedico.split(" ")[0] : nomeMedico);

        String emailTemplate = emailService.loadTemplate(TEMPLATE_EMAIL_LEMBRETE);
        String corpoMensagem = "";
        try {
            LocalDateTime dataHora = consulta.dataConsulta();
            String horarioConsulta = dataHora.format(DateTimeFormatter.ofPattern("HH'h'mm"));
            corpoMensagem = String.format(emailTemplate, nomePaciente, horarioConsulta, nomeMedico);
        } catch (DateTimeParseException ex) {
            log.error("Erro no formato da data e hora", ex);
        }

        Notificacao nova = new Notificacao();
        nova.setIdConsulta(consulta.uuid());
        nova.setMensagem(corpoMensagem);
        nova.setSituacao(SituacaoNotificacao.PENDENTE);
        Notificacao salva = notificacaoService.create(nova);
        log.info("Notificação salva! Consulta ID: " + salva.getIdConsulta());

        try {
            emailService.enviar(consulta.pacienteEmail(), "Lembrete de Consulta", corpoMensagem);
            salva.setDataNotificacao(LocalDateTime.now());
            salva.setSituacao(SituacaoNotificacao.ENVIADA);
        } catch (Exception ex) {
            log.error("Erro no envio do e-mail", ex);
            salva.setSituacao(SituacaoNotificacao.ERRO);
        } finally {
            notificacaoService.update(salva);
        }
    }
}
