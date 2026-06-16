package at.fhtw.disys.energyproducer.service;

import at.fhtw.disys.energyproducer.messaging.EnergyAssociation;
import at.fhtw.disys.energyproducer.messaging.EnergyMessage;
import at.fhtw.disys.energyproducer.messaging.EnergyMessageType;
import at.fhtw.disys.energyproducer.messaging.RabbitMqNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class EnergyProducerMessagePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final WeatherProductionFactorService weatherProductionFactorService;
    private final long minDelayMs;
    private final long maxDelayMs;

    public EnergyProducerMessagePublisher(
            RabbitTemplate rabbitTemplate,
            WeatherProductionFactorService weatherProductionFactorService,
            @Value("${energy.producer.min-delay-ms:1000}") long minDelayMs,
            @Value("${energy.producer.max-delay-ms:5000}") long maxDelayMs
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.weatherProductionFactorService = weatherProductionFactorService;
        this.minDelayMs = minDelayMs;
        this.maxDelayMs = maxDelayMs;
    }

    @Scheduled(fixedDelay = 1)
    public void publishProductionMessage() {
        try {
            waitRandomDelay();
            double sunFactor = weatherProductionFactorService.currentSunFactor();
            double baseKwhPerMinute = ThreadLocalRandom.current().nextDouble(0.015, 0.075);
            double producedKwh = round(baseKwhPerMinute * sunFactor);

            EnergyMessage message = new EnergyMessage(
                    EnergyMessageType.PRODUCER,
                    EnergyAssociation.COMMUNITY,
                    producedKwh,
                    LocalDateTime.now()
            );

            rabbitTemplate.convertAndSend(
                    RabbitMqNames.ENERGY_EXCHANGE,
                    RabbitMqNames.ENERGY_MESSAGE_ROUTING_KEY,
                    message
            );
            log.info("Published producer message: {} kWh", producedKwh);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void waitRandomDelay() throws InterruptedException {
        long delayMs = ThreadLocalRandom.current().nextLong(minDelayMs, maxDelayMs + 1);
        Thread.sleep(delayMs);
    }

    private double round(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}
