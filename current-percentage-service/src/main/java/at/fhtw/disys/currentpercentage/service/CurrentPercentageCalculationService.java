package at.fhtw.disys.currentpercentage.service;

import at.fhtw.disys.currentpercentage.entity.CurrentPercentage;
import at.fhtw.disys.currentpercentage.repository.CurrentPercentageRepository;
import at.fhtw.disys.currentpercentage.messaging.UsageUpdatedMessage;
import at.fhtw.disys.currentpercentage.messaging.RabbitMqNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrentPercentageCalculationService {

    private final CurrentPercentageRepository currentPercentageRepository;

    @Transactional
    @RabbitListener(queues = RabbitMqNames.USAGE_UPDATES_QUEUE)
    public void handleUsageUpdated(UsageUpdatedMessage message) {
        CurrentPercentage currentPercentage = new CurrentPercentage(
                message.hour(),
                calculateCommunityDepleted(message.communityUsed(), message.communityProduced()),
                calculateGridPortion(message.communityUsed(), message.gridUsed())
        );

        currentPercentageRepository.deleteAll();
        currentPercentageRepository.save(currentPercentage);
        log.info("Updated current percentage for {}: communityDepleted={}, gridPortion={}",
                currentPercentage.getHour(),
                currentPercentage.getCommunityDepleted(),
                currentPercentage.getGridPortion());
    }

    private double calculateCommunityDepleted(double communityUsed, double communityProduced) {
        if (communityProduced == 0) return 0;
        return round((communityUsed / communityProduced) * 100.0);
    }

    private double calculateGridPortion(double communityUsed, double gridUsed) {
        double totalUsed = communityUsed + gridUsed;
        if (totalUsed == 0) return 0;
        return round((gridUsed / totalUsed) * 100.0);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
