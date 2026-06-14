package at.fhtw.disys.shared.message;

import java.time.LocalDateTime;

public record EnergyMessage(
        EnergyMessageType type,
        EnergyAssociation association,
        double kwh,
        LocalDateTime datetime
) {
}
