package at.fhtw.disys.currentpercentage.repository;

import at.fhtw.disys.currentpercentage.entity.HourlyUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface HourlyUsageRepository extends JpaRepository<HourlyUsage, LocalDateTime> {
}
