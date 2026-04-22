package at.fhtw.disys.uijavafx.controller;

import at.fhtw.disys.uijavafx.model.EnergyDataDto;
import at.fhtw.disys.uijavafx.service.EnergyRestClientService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

public class MainViewController {

    private final EnergyRestClientService energyRestClientService = new EnergyRestClientService();

    @FXML private Label communityPoolUsageValueLabel;
    @FXML private Label gridPortionValueLabel;

    @FXML private TextField fromTextField;
    @FXML private TextField toTextField;

    @FXML private TableView<EnergyDataDto> historicTable;
    @FXML private TableColumn<EnergyDataDto, String> colTimestamp;
    @FXML private TableColumn<EnergyDataDto, String> colProduced;
    @FXML private TableColumn<EnergyDataDto, String> colUsed;
    @FXML private TableColumn<EnergyDataDto, String> colGrid;

    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        colTimestamp.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTimestamp()));
        colProduced.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.3f", data.getValue().getProducedKwh())));
        colUsed.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.3f", data.getValue().getSelfConsumedKwh())));
        colGrid.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.3f", data.getValue().getGridImportKwh())));
    }

    @FXML
    public void onRefreshCurrentData() {
        statusLabel.setText("Loading...");
        new Thread(() -> {
            try {
                EnergyDataDto data = energyRestClientService.getCurrentHourData();
                double communityDepleted = calculateCommunityDepleted(data);
                double gridPortion = calculateGridPortion(data);
                Platform.runLater(() -> {
                    communityPoolUsageValueLabel.setText(String.format("%.2f %%", communityDepleted));
                    gridPortionValueLabel.setText(String.format("%.2f %%", gridPortion));
                    statusLabel.setText("Current data loaded successfully.");
                });
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Error loading current data: " + e.getClass().getSimpleName()));
            }
        }).start();
    }

    @FXML
    public void onShowHistoricData() {
        statusLabel.setText("Loading...");
        new Thread(() -> {
            try {
                List<EnergyDataDto> data = energyRestClientService.getHistoricalData(
                        fromTextField.getText(), toTextField.getText()
                );
                Platform.runLater(() -> {
                    historicTable.getItems().setAll(data);
                    statusLabel.setText("Historical data loaded: " + data.size() + " records.");
                });
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Error loading historical data: " + e.getClass().getSimpleName()));
            }
        }).start();
    }

    private double calculateCommunityDepleted(EnergyDataDto data) {
        if (data.getProducedKwh() == 0) return 0;
        return (data.getSelfConsumedKwh() / data.getProducedKwh()) * 100;
    }

    private double calculateGridPortion(EnergyDataDto data) {
        if (data.getConsumedKwh() == 0) return 0;
        return (data.getGridImportKwh() / data.getConsumedKwh()) * 100;
    }
}