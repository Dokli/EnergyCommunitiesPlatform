package at.fhtw.disys.currentpercentage.service;

import at.fhtw.disys.currentpercentage.entity.HourlyUsage;
import at.fhtw.disys.currentpercentage.repository.HourlyUsageRepository;
import at.fhtw.disys.shared.message.EnergyMessage;
import at.fhtw.disys.shared.message.EnergyMessageType;
import at.fhtw.disys.shared.message.UsageUpdatedMessage;
import at.fhtw.disys.shared.rabbit.RabbitMqNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsageAggregationService {

    private final HourlyUsageRepository hourlyUsageRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    @RabbitListener(queues = RabbitMqNames.ENERGY_MESSAGES_QUEUE)
    public void handleEnergyMessage(EnergyMessage message) {
        LocalDateTime hour = truncateToHour(message.datetime());
        HourlyUsage usage = hourlyUsageRepository.findById(hour)
                .orElseGet(() -> new HourlyUsage(hour, 0, 0, 0));

        if (message.type() == EnergyMessageType.PRODUCER) {
            usage.setCommunityProduced(round(usage.getCommunityProduced() + message.kwh()));
        } else if (message.type() == EnergyMessageType.USER) {
            applyCommunityUsage(usage, message.kwh());
        }

        hourlyUsageRepository.save(usage);
        publishUsageUpdated(hour);
        log.info("Updated hourly usage for {}: produced={}, communityUsed={}, gridUsed={}",
                hour, usage.getCommunityProduced(), usage.getCommunityUsed(), usage.getGridUsed());
    }

    private void applyCommunityUsage(HourlyUsage usage, double requestedKwh) {
        double availableCommunityEnergy = Math.max(0, usage.getCommunityProduced() - usage.getCommunityUsed());
        double communityShare = Math.min(requestedKwh, availableCommunityEnergy);
        double gridShare = requestedKwh - communityShare;

        usage.setCommunityUsed(round(usage.getCommunityUsed() + communityShare));
        usage.setGridUsed(round(usage.getGridUsed() + gridShare));
    }

    private void publishUsageUpdated(LocalDateTime hour) {
        rabbitTemplate.convertAndSend(
                RabbitMqNames.ENERGY_EXCHANGE,
                RabbitMqNames.USAGE_UPDATED_ROUTING_KEY,
                new UsageUpdatedMessage(hour)
        );
    }

    private LocalDateTime truncateToHour(LocalDateTime dateTime) {
        return dateTime.withMinute(0).withSecond(0).withNano(0);
    }

    private double round(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}
