package at.fhtw.disys.energycommunitiesplatformrestapi.service;

import org.springframework.stereotype.Service;

@Service
public class EnergyCalculationService {
    public double calculateSelfConsumed(double producedKwh, double consumedKwh) {
        return Math.min(producedKwh, consumedKwh);
    }

    public double calculateGridImport(double producedKwh, double consumedKwh) {
        return Math.max(0, consumedKwh - producedKwh);
    }

    public double calculateGridExport(double producedKwh, double consumedKwh) {
        return Math.max(0, producedKwh - consumedKwh);
    }
}
