package at.fhtw.disys.shared.message;

import java.time.LocalDateTime;

public record UsageUpdatedMessage(LocalDateTime hour) {
}
