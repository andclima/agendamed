package br.com.fiap.msnotificacao.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${spring.mail.username}")
    private String remetente;

    private final JavaMailSender mailSender;

    @Async("taskExecutor")
    public void enviar(String destinatario, String assunto, String corpo) {
        log.info("Iniciando envio de e-mail para: {}", destinatario);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(remetente);
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(corpo, true);
            mailSender.send(message);
            log.info("E-mail enviado com sucesso na thread: {}", Thread.currentThread().getName());
        } catch (MessagingException e) {
            log.error("Falha ao enviar e-mail para {}: {}", destinatario, e.getMessage());
        }
    }

    public String loadTemplate(String file) {
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(file)) {
            if (inputStream == null) {
                throw new RuntimeException("Arquivo de template não encontrado: " + file);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler template de e-mail", e);
        }
    }
}