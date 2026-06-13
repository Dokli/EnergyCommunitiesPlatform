package at.fhtw.disys.currentpercentage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "current_percentage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentPercentage {

    @Id
    @Column(name = "hour", nullable = false)
    private LocalDateTime hour;

    @Column(name = "community_depleted", nullable = false)
    private double communityDepleted;

    @Column(name = "grid_portion", nullable = false)
    private double gridPortion;
}