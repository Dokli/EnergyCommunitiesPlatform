package at.fhtw.disys.energyproducer.messaging;

import java.time.LocalDateTime;

public record EnergyMessage(
        EnergyMessageType type,
        EnergyAssociation association,
        double kwh,
        LocalDateTime datetime
) {
}