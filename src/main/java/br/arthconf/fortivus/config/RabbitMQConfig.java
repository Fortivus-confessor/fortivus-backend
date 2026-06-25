package br.arthconf.fortivus.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String FIRE_EVENTS_EXCHANGE = "fortivus.fire_events";
    public static final String SEVERE_EVENT_QUEUE = "fortivus.q.fire_events.severe";
    public static final String ROUTING_KEY_SEVERE = "fire.detected.severe";

    public static final String ATTACHMENT_EXCHANGE = "attachment.exchange";
    public static final String ATTACHMENT_QUEUE = "fortivus.q.attachments";
    public static final String ROUTING_KEY_ATTACHMENT = "attachment.#";

    // Fire events
    @Bean
    public TopicExchange fireEventsExchange() {
        return new TopicExchange(FIRE_EVENTS_EXCHANGE);
    }

    @Bean
    public Queue severeEventQueue() {
        return new Queue(SEVERE_EVENT_QUEUE, true);
    }

    @Bean
    public Binding severeEventBinding(Queue severeEventQueue, TopicExchange fireEventsExchange) {
        return BindingBuilder.bind(severeEventQueue).to(fireEventsExchange).with(ROUTING_KEY_SEVERE);
    }

    // Attachment events
    @Bean
    public TopicExchange attachmentExchange() {
        return new TopicExchange(ATTACHMENT_EXCHANGE);
    }

    @Bean
    public Queue attachmentQueue() {
        return new Queue(ATTACHMENT_QUEUE, true);
    }

    @Bean
    public Binding attachmentBinding(Queue attachmentQueue, TopicExchange attachmentExchange) {
        return BindingBuilder.bind(attachmentQueue).to(attachmentExchange).with(ROUTING_KEY_ATTACHMENT);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
