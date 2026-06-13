package at.fhtw.disys.energycommunitiesplatformrestapi.repository;

import at.fhtw.disys.energycommunitiesplatformrestapi.entity.CurrentPercentage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CurrentPercentageRepository extends JpaRepository<CurrentPercentage, LocalDateTime> {

    Optional<CurrentPercentage> findTopByOrderByHourDesc();
}