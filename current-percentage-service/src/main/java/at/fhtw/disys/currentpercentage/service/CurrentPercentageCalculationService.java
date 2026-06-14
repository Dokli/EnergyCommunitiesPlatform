package at.fhtw.disys.currentpercentage.service;

import at.fhtw.disys.currentpercentage.entity.CurrentPercentage;
import at.fhtw.disys.currentpercentage.entity.HourlyUsage;
import at.fhtw.disys.currentpercentage.repository.CurrentPercentageRepository;
import at.fhtw.disys.currentpercentage.repository.HourlyUsageRepository;
import at.fhtw.disys.shared.message.UsageUpdatedMessage;
import at.fhtw.disys.shared.rabbit.RabbitMqNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrentPercentageCalculationService {

    private final HourlyUsageRepository hourlyUsageRepository;
    private final CurrentPercentageRepository currentPercentageRepository;

    @Transactional
    @RabbitListener(queues = RabbitMqNames.USAGE_UPDATES_QUEUE)
    public void handleUsageUpdated(UsageUpdatedMessage message) {
        hourlyUsageRepository.findById(message.hour()).ifPresent(usage -> {
            CurrentPercentage currentPercentage = new CurrentPercentage(
                    usage.getHour(),
                    calculateCommunityDepleted(usage),
                    calculateGridPortion(usage)
            );

            currentPercentageRepository.deleteAll();
            currentPercentageRepository.save(currentPercentage);
            log.info("Updated current percentage for {}: communityDepleted={}, gridPortion={}",
                    currentPercentage.getHour(),
                    currentPercentage.getCommunityDepleted(),
                    currentPercentage.getGridPortion());
        });
    }

    private double calculateCommunityDepleted(HourlyUsage usage) {
        if (usage.getCommunityProduced() == 0) {
            return 0;
        }
        return round((usage.getCommunityUsed() / usage.getCommunityProduced()) * 100.0);
    }

    private double calculateGridPortion(HourlyUsage usage) {
        double totalUsed = usage.getCommunityUsed() + usage.getGridUsed();
        if (totalUsed == 0) {
            return 0;
        }
        return round((usage.getGridUsed() / totalUsed) * 100.0);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
