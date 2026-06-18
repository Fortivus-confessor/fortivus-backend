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

    public static final String EXCHANGE_NAME = "fortivus.fire_events";
    public static final String SEVERE_EVENT_QUEUE = "fortivus.q.fire_events.severe";
    public static final String ROUTING_KEY_SEVERE = "fire.detected.severe";

    @Bean
    public TopicExchange fireEventsExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue severeEventQueue() {
        return new Queue(SEVERE_EVENT_QUEUE, true);
    }

    @Bean
    public Binding bindingSevereEvent(Queue severeEventQueue, TopicExchange fireEventsExchange) {
        return BindingBuilder.bind(severeEventQueue).to(fireEventsExchange).with(ROUTING_KEY_SEVERE);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
