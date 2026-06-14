package at.fhtw.disys.currentpercentage.config;

import at.fhtw.disys.shared.rabbit.RabbitMqNames;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
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
    Queue usageUpdatesQueue() {
        return new Queue(RabbitMqNames.USAGE_UPDATES_QUEUE, true);
    }

    @Bean
    Binding usageUpdatesBinding(Queue usageUpdatesQueue, DirectExchange energyExchange) {
        return BindingBuilder.bind(usageUpdatesQueue)
                .to(energyExchange)
                .with(RabbitMqNames.USAGE_UPDATED_ROUTING_KEY);
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }
}
