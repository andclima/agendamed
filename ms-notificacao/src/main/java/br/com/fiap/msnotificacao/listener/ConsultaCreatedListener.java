package br.com.fiap.msnotificacao.listener;

import br.com.fiap.msnotificacao.model.Notificacao;
import br.com.fiap.msnotificacao.model.enums.SituacaoNotificacao;
import br.com.fiap.msnotificacao.service.EmailService;
import br.com.fiap.msnotificacao.service.NotificacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static br.com.fiap.msnotificacao.config.RabbitMqConfig.CONSULTA_CREATED;

@Component
public class ConsultaCreatedListener {

    private final String TEMPLATE_EMAIL_AGENDAMENTO = "templates/email-agendamento.html";
    private final Logger log = LoggerFactory.getLogger(ConsultaCreatedListener.class);
    private final NotificacaoService notificacaoService;
    private final EmailService emailService;

    public ConsultaCreatedListener(NotificacaoService notificacaoService, EmailService emailService) {
        this.notificacaoService = notificacaoService;
        this.emailService = emailService;
    }

    @RabbitListener(queues =  CONSULTA_CREATED)
    public void listen(Message<ConsultaCreatedEvent> message) {
        ConsultaCreatedEvent event = message.getPayload();

        String nomePaciente = event.nomePaciente();
        nomePaciente = (nomePaciente.contains(" ") ? nomePaciente.split(" ")[0] : nomePaciente);
        String nomeMedico = event.nomeMedico();
        nomeMedico = (nomeMedico.contains(" ") ? nomeMedico.split(" ")[0] : nomeMedico);

        String emailTemplate = emailService.loadTemplate(TEMPLATE_EMAIL_AGENDAMENTO);
        String corpoMensagem = "";
        try {
            LocalDateTime dataHora = LocalDateTime.parse(event.dataHora());
            String dataConsulta = dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String horarioConsulta = dataHora.format(DateTimeFormatter.ofPattern("HH'h'mm"));
            corpoMensagem = String.format(emailTemplate, nomePaciente, dataConsulta, horarioConsulta, nomeMedico);
        } catch (DateTimeParseException ex) {
            log.error("Erro no formato da data e hora da consulta no RabbitMQ", ex);
        }

        Notificacao nova = new Notificacao();
        nova.setIdConsulta(event.consultaId());
        nova.setMensagem(corpoMensagem);
        nova.setSituacao(SituacaoNotificacao.PENDENTE);
        Notificacao salva = notificacaoService.create(nova);
        log.info("Notificação salva! Consulta ID: " + salva.getIdConsulta());

        try {
            emailService.enviar(event.emailPaciente(), "Agendamento de Consulta", corpoMensagem);
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
