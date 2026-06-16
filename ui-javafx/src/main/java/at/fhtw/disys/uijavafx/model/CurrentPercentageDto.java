package at.fhtw.disys.uijavafx.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentPercentageDto {
    private String timestamp;
    private double communityDepleted;
    private double gridPortion;
}
