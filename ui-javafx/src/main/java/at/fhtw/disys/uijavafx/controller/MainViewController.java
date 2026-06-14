package at.fhtw.disys.uijavafx.controller;

import at.fhtw.disys.uijavafx.model.CurrentPercentageDto;
import at.fhtw.disys.uijavafx.model.EnergyDataDto;
import at.fhtw.disys.uijavafx.service.EnergyRestClientException;
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
        fromTextField.setPromptText("yyyy-MM-dd or yyyy-MM-ddTHH:mm");
        toTextField.setPromptText("yyyy-MM-dd or yyyy-MM-ddTHH:mm");
    }

    @FXML
    public void onRefreshCurrentData() {
        statusLabel.setText("Loading...");
        new Thread(() -> {
            try {
                CurrentPercentageDto data = energyRestClientService.getCurrentHourData();
                Platform.runLater(() -> {
                    communityPoolUsageValueLabel.setText(String.format("%.2f %%", data.getCommunityDepleted()));
                    gridPortionValueLabel.setText(String.format("%.2f %%", data.getGridPortion()));
                    statusLabel.setText("Current data loaded successfully.");
                });
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Error loading current data: " + errorMessage(e)));
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
                Platform.runLater(() -> statusLabel.setText("Error loading historical data: " + errorMessage(e)));
            }
        }).start();
    }

    private String errorMessage(Exception exception) {
        if (exception instanceof EnergyRestClientException) {
            return exception.getMessage();
        }
        return exception.getClass().getSimpleName();
    }
}
