package sk.upjs.paz.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class RegisterController {

    @FXML
    private BorderPane root;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Hyperlink loginLink;

    private final ThemeManager themeManager = new ThemeManager();

    @FXML
    private void initialize() {
        themeManager.applyTheme(root);
    }

    @FXML
    private void onRegister() {
        if (nameField == null || emailField == null || passwordField == null || confirmPasswordField == null) {
            return;
        }

        if (nameField.getText().isBlank() || emailField.getText().isBlank() ||
                passwordField.getText().isBlank() || confirmPasswordField.getText().isBlank()) {
            showValidation("All fields are required.");
            return;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showValidation("Passwords do not match.");
            return;
        }

        // TODO integrate with registration service
        System.out.printf("Register user %s (%s)\n", nameField.getText(), emailField.getText());
    }

    @FXML
    private void onOpenLogin() {
        // TODO navigate to login scene
        if (loginLink != null) {
            loginLink.setVisited(false);
        }
        System.out.println("Navigate to login");
    }

    private void showValidation(String message) {
        var alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Missing information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
