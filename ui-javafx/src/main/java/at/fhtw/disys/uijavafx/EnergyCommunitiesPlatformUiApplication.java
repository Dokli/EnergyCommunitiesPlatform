package at.fhtw.disys.uijavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EnergyCommunitiesPlatformUiApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(
                EnergyCommunitiesPlatformUiApplication.class.getResource("/at/fhtw/disys/uijavafx/main-view.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 750, 620);

        stage.setTitle("Energy Communities Platform");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
