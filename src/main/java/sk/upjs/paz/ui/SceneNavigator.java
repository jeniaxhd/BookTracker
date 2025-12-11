package sk.upjs.paz.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneNavigator {

    private static Stage mainStage;

    public static void setMainStage(Stage stage) {
        mainStage = stage;
    }

    public static void showLogin() {
        loadScene("login.fxml", "BookTrack – Login");
    }

    public static void showRegister() {
        loadScene("register.fxml", "BookTrack – Register");
    }

    public static void showDashboard() {
        loadScene("dashboard.fxml", "BookTrack – Dashboard");
    }

    private static void loadScene(String fxmlName, String title) {
        if (mainStage == null) {
            throw new IllegalStateException("Main stage is not set.");
        }

        try {
            // FXML are in src/main/resources/fxml
            FXMLLoader loader = new FXMLLoader(
                    SceneNavigator.class.getResource("  /fxml/" + fxmlName)
            );

            Parent root = loader.load();
            Scene scene = new Scene(root);
            mainStage.setTitle(title);
            mainStage.setScene(scene);
            mainStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Cannot load FXML: " + fxmlName, e);
        }
    }
}
