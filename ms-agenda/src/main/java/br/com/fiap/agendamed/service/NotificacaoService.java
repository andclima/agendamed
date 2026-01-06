package br.com.fiap.agendamed.service;

import br.com.fiap.agendamed.config.ConsultaCreatedEvent;
import br.com.fiap.agendamed.config.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final RabbitTemplate rabbitTemplate;
    private final Logger log = LoggerFactory.getLogger(NotificacaoService.class);

    public void enviar(ConsultaCreatedEvent mensagem) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE,
                RabbitMqConfig.ROUTING_KEY,
                mensagem
        );
        log.info("Evento notificação enviado! Consulta ID: " + mensagem.consultaId());
    }
}
