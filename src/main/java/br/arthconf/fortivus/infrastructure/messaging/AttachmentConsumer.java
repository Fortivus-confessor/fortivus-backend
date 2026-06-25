package br.arthconf.fortivus.infrastructure.messaging;

import br.arthconf.fortivus.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AttachmentConsumer {

    @RabbitListener(queues = RabbitMQConfig.ATTACHMENT_QUEUE)
    public void receberEventoAnexo(
            String attachmentId,
            @Header("amqp_receivedRoutingKey") String routingKey) {
        log.info("[ANEXO] Evento recebido: routingKey={} attachmentId={}", routingKey, attachmentId);
    }
}
