package at.fhtw.disys.energyuser.service;

import at.fhtw.disys.energyuser.messaging.EnergyAssociation;
import at.fhtw.disys.energyuser.messaging.EnergyMessage;
import at.fhtw.disys.energyuser.messaging.EnergyMessageType;
import at.fhtw.disys.energyuser.messaging.RabbitMqNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class EnergyUserMessagePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final long minDelayMs;
    private final long maxDelayMs;

    public EnergyUserMessagePublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${energy.user.min-delay-ms:1000}") long minDelayMs,
            @Value("${energy.user.max-delay-ms:5000}") long maxDelayMs
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.minDelayMs = minDelayMs;
        this.maxDelayMs = maxDelayMs;
    }

    @Scheduled(fixedDelay = 1)
    public void publishUsageMessage() {
        try {
            waitRandomDelay();
            double baseKwhPerMinute = ThreadLocalRandom.current().nextDouble(0.01, 0.055);
            double usedKwh = round(baseKwhPerMinute * timeOfDayFactor(LocalTime.now()));

            EnergyMessage message = new EnergyMessage(
                    EnergyMessageType.USER,
                    EnergyAssociation.COMMUNITY,
                    usedKwh,
                    LocalDateTime.now()
            );

            rabbitTemplate.convertAndSend(
                    RabbitMqNames.ENERGY_EXCHANGE,
                    RabbitMqNames.ENERGY_MESSAGE_ROUTING_KEY,
                    message
            );
            log.info("Published user message: {} kWh", usedKwh);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void waitRandomDelay() throws InterruptedException {
        long delayMs = ThreadLocalRandom.current().nextLong(minDelayMs, maxDelayMs + 1);
        Thread.sleep(delayMs);
    }

    private double timeOfDayFactor(LocalTime time) {
        int hour = time.getHour();
        if ((hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 21)) {
            return 1.6;
        }
        if (hour <= 5) {
            return 0.45;
        }
        return 1.0;
    }

    private double round(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}
