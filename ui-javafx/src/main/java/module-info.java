module at.fhtw.disys.uijavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens at.fhtw.disys.uijavafx to javafx.fxml;
    exports at.fhtw.disys.uijavafx;
}