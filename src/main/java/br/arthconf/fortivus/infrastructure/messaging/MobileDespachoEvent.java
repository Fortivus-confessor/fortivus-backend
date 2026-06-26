package br.arthconf.fortivus.infrastructure.messaging;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payload publicado no exchange mobile quando um despacho é criado/atribuído.
 * Consumidores de notificação (FCM, WebSocket) lêem desta fila.
 */
public record MobileDespachoEvent(
        Long despachoId,
        Long ordemServicoId,
        UUID responsavelId,
        String categoria,
        String descricaoTarefa,
        LocalDateTime dataInicio
) {}
