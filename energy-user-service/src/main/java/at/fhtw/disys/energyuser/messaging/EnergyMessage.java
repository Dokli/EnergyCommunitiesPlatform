package at.fhtw.disys.energyuser.messaging;

import java.time.LocalDateTime;

public record EnergyMessage(
        EnergyMessageType type,
        EnergyAssociation association,
        double kwh,
        LocalDateTime datetime
) {
}