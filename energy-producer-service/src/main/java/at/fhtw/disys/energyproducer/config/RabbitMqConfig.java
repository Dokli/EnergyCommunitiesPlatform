package at.fhtw.disys.energyproducer.config;

import at.fhtw.disys.shared.rabbit.RabbitMqNames;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    DirectExchange energyExchange() {
        return new DirectExchange(RabbitMqNames.ENERGY_EXCHANGE);
    }

    @Bean
    Queue energyMessagesQueue() {
        return new Queue(RabbitMqNames.ENERGY_MESSAGES_QUEUE, true);
    }

    @Bean
    Binding energyMessagesBinding(Queue energyMessagesQueue, DirectExchange energyExchange) {
        return BindingBuilder.bind(energyMessagesQueue)
                .to(energyExchange)
                .with(RabbitMqNames.ENERGY_MESSAGE_ROUTING_KEY);
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
