package at.fhtw.disys.currentpercentage.messaging;

import java.time.LocalDateTime;

public record UsageUpdatedMessage(
        LocalDateTime hour,
        double communityProduced,
        double communityUsed,
        double gridUsed
) {}
