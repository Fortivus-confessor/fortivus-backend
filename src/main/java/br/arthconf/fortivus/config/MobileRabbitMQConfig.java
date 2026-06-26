package br.arthconf.fortivus.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Exchange e filas exclusivas do tráfego mobile.
 * Isolamento evita contaminação de filas web e permite throttling independente.
 *
 * Topologia:
 *   Exchange: fortivus.mobile (Topic)
 *     └─ mobile.despacho.atribuido  →  fortivus.q.mobile.despacho.atribuido
 *        (Dead-letter → fortivus.q.mobile.dlq após 3 tentativas)
 */
@Configuration
public class MobileRabbitMQConfig {

    public static final String MOBILE_EXCHANGE            = "fortivus.mobile";
    public static final String DESPACHO_ASSIGNED_QUEUE    = "fortivus.q.mobile.despacho.atribuido";
    public static final String DESPACHO_ASSIGNED_DLQ      = "fortivus.q.mobile.dlq";
    public static final String ROUTING_DESPACHO_ATRIBUIDO = "mobile.despacho.atribuido";

    @Bean
    public TopicExchange mobileExchange() {
        return new TopicExchange(MOBILE_EXCHANGE, true, false);
    }

    @Bean
    public Queue mobileDespachoAtribuidoDlq() {
        return QueueBuilder.durable(DESPACHO_ASSIGNED_DLQ).build();
    }

    @Bean
    public Queue mobileDespachoAtribuidoQueue(Queue mobileDespachoAtribuidoDlq) {
        return QueueBuilder.durable(DESPACHO_ASSIGNED_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", DESPACHO_ASSIGNED_DLQ)
                .withArgument("x-message-ttl", 86_400_000) // 24h TTL
                .build();
    }

    @Bean
    public Binding mobileDespachoAtribuidoBinding(
            Queue mobileDespachoAtribuidoQueue,
            TopicExchange mobileExchange) {
        return BindingBuilder
                .bind(mobileDespachoAtribuidoQueue)
                .to(mobileExchange)
                .with(ROUTING_DESPACHO_ATRIBUIDO);
    }
}
