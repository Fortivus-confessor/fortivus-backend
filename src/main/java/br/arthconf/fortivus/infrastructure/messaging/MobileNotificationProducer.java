package br.arthconf.fortivus.infrastructure.messaging;

import br.arthconf.fortivus.config.MobileRabbitMQConfig;
import br.arthconf.fortivus.domain.model.Despacho;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MobileNotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Publica evento de despacho atribuído de forma assíncrona.
     * Async para não bloquear a transação principal — fire-and-forget.
     */
    @Async
    public void publicarDespachoAtribuido(Despacho despacho) {
        if (despacho.getResponsavelId() == null) return;

        var event = new MobileDespachoEvent(
                despacho.getId(),
                despacho.getOrdemServicoId(),
                despacho.getResponsavelId(),
                despacho.getCategoria() != null ? despacho.getCategoria().name() : null,
                despacho.getDescricaoTarefa(),
                despacho.getDataInicio()
        );

        try {
            rabbitTemplate.convertAndSend(
                    MobileRabbitMQConfig.MOBILE_EXCHANGE,
                    MobileRabbitMQConfig.ROUTING_DESPACHO_ATRIBUIDO,
                    event
            );
            log.debug("[Mobile] Evento publicado para responsável={}", despacho.getResponsavelId());
        } catch (Exception e) {
            log.error("[Mobile] Falha ao publicar evento de despacho {}: {}", despacho.getId(), e.getMessage());
        }
    }
}
