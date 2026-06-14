package at.fhtw.disys.energyproducer.service;

import at.fhtw.disys.energyproducer.messaging.EnergyAssociation;
import at.fhtw.disys.energyproducer.messaging.EnergyMessage;
import at.fhtw.disys.energyproducer.messaging.EnergyMessageType;
import at.fhtw.disys.energyproducer.messaging.RabbitMqNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnergyProducerMessagePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final WeatherProductionFactorService weatherProductionFactorService;

    @Scheduled(fixedRateString = "${energy.producer.fixed-rate-ms}")
    public void publishProductionMessage() {
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
    }

    private double round(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}
