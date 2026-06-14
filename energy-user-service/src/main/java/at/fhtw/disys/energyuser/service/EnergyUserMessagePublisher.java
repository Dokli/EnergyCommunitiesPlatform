package at.fhtw.disys.energyuser.service;

import at.fhtw.disys.shared.message.EnergyAssociation;
import at.fhtw.disys.shared.message.EnergyMessage;
import at.fhtw.disys.shared.message.EnergyMessageType;
import at.fhtw.disys.shared.rabbit.RabbitMqNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnergyUserMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedRateString = "${energy.user.fixed-rate-ms}")
    public void publishUsageMessage() {
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
    }

    private double timeOfDayFactor(LocalTime time) {
        int hour = time.getHour();
        if ((hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 21)) {
            return 1.6;
        }
        if (hour >= 0 && hour <= 5) {
            return 0.45;
        }
        return 1.0;
    }

    private double round(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}
