package sk.upjs.paz.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class LoginController {

    @FXML
    private BorderPane root;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox rememberMeCheckBox;

    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    private Hyperlink createAccountLink;

    private final ThemeManager themeManager = new ThemeManager();

    @FXML
    private void initialize() {
        themeManager.applyTheme(root);
        // simple safe defaults
        if (rememberMeCheckBox != null) {
            rememberMeCheckBox.setSelected(true);
        }
    }

    @FXML
    private void onSignIn() {
        if (emailField == null || passwordField == null) {
            return;
        }

        var email = emailField.getText();
        var password = passwordField.getText();

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Incomplete");
            alert.setHeaderText("Please fill in both email and password.");
            alert.showAndWait();
            return;
        }

        // TODO replace with auth service call
        System.out.printf("Attempt login for %s (remember=%s)\n", email, rememberMeCheckBox != null && rememberMeCheckBox.isSelected());
    }

    @FXML
    private void onForgotPassword() {
        // TODO implement password recovery flow
        System.out.println("Forgot password clicked");
    }

    @FXML
    private void onCreateAccount() {
        // TODO navigate to register scene
        if (createAccountLink != null) {
            createAccountLink.setVisited(false);
        }
        System.out.println("Navigate to registration");
    }
}
