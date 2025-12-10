package sk.upjs.paz.ui;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class DashboardController {

    @FXML
    private BorderPane root; // це fx:id="root" з FXML

    private boolean dark = false;

    @FXML
    private void initialize() {
        // старт — світла тема
        root.getStylesheets().add(
                getClass().getResource("/css/lightTheme.css").toExternalForm()
        );
    }

    @FXML
    private void onToggleTheme() {
        var stylesheets = root.getStylesheets();
        stylesheets.clear();

        if (dark) {
            // назад на світлу
            stylesheets.add(
                    getClass().getResource("/css/lightTheme.css").toExternalForm()
            );
        } else {
            // темна
            stylesheets.add(
                    getClass().getResource("/css/darkTheme.css").toExternalForm()
            );
        }

        dark = !dark;
    }
}
