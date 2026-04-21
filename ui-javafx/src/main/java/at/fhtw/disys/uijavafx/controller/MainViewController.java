package at.fhtw.disys.uijavafx.controller;

import at.fhtw.disys.uijavafx.model.EnergyDataDto;
import at.fhtw.disys.uijavafx.service.EnergyRestClientService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MainViewController {

    private final EnergyRestClientService energyRestClientService = new EnergyRestClientService();

    @FXML
    private Label communityPoolUsageValueLabel;

    @FXML
    private Label gridPortionValueLabel;

    @FXML
    private TextField fromTextField;

    @FXML
    private TextField toTextField;

    @FXML
    private Label communityProducedValueLabel;

    @FXML
    private Label communityUsedValueLabel;

    @FXML
    private Label gridUsedValueLabel;

    @FXML
    private Label statusLabel;

    @FXML
    public void onRefreshCurrentData() {
        System.out.println("onRefreshCurrentData called");

        try {
            EnergyDataDto data = energyRestClientService.getCurrentHourData();
            System.out.println("DTO received: " + data);

            double communityPoolUsagePercent = calculateCommunityPoolUsagePercent(data);
            double gridPortionPercent = calculateGridPortionPercent(data);

            communityPoolUsageValueLabel.setText(String.format("%.2f %%", communityPoolUsagePercent));
            gridPortionValueLabel.setText(String.format("%.2f %%", gridPortionPercent));
            statusLabel.setText("Current data loaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error loading current data: " + e.getClass().getSimpleName());
        }
    }

    @FXML
    public void onShowHistoricData() {
        statusLabel.setText("Historic data not connected yet.");
    }

    private double calculateCommunityPoolUsagePercent(EnergyDataDto data) {
        if (data.getConsumedKwh() == 0) {
            return 0;
        }
        return (data.getSelfConsumedKwh() / data.getConsumedKwh()) * 100;
    }

    private double calculateGridPortionPercent(EnergyDataDto data) {
        if (data.getConsumedKwh() == 0) {
            return 0;
        }
        return (data.getGridImportKwh() / data.getConsumedKwh()) * 100;
    }
}
