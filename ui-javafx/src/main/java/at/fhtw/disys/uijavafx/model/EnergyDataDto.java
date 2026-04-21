package at.fhtw.disys.uijavafx.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnergyDataDto {
    private String timestamp;
    private double producedKwh;
    private double consumedKwh;
    private double selfConsumedKwh;
    private double gridImportKwh;
    private double gridExportKwh;
}
