package br.arthconf.fortivus.infrastructure.messaging;

import br.arthconf.fortivus.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FireEventConsumer {

    @RabbitListener(queues = RabbitMQConfig.SEVERE_EVENT_QUEUE)
    public void receberEventoSevero(EventoSeveroMessage evento) {
        log.warn(
            "[ALERTA] Evento de fogo severo detectado: id={} severidade={} frp={} focos={} lat={} lng={}",
            evento.getEventoId(),
            evento.getSeveridade(),
            evento.getFrpTotal(),
            evento.getTotalFocos(),
            evento.getLatitudeCentroide(),
            evento.getLongitudeCentroide()
        );
    }
}
