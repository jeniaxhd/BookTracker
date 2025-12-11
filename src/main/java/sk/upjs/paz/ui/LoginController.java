package sk.upjs.paz.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox rememberMeCheckBox;

    @FXML
    private Label errorLabel;

    @FXML
    private Button signInButton;

    @FXML
    private Button googleButton;

    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    private Hyperlink createAccountLink;

    @FXML
    private void initialize() {
        // Hide error label on start
        if (errorLabel != null) {
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }
    }

    @FXML
    private void onSignIn(ActionEvent event) {
        clearError();

        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String password = passwordField.getText() != null ? passwordField.getText() : "";

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill in both email and password.");
            return;
        }

        // TODO: Add real authentication (e.g. check against database / service)
        boolean authenticated = fakeAuthentication(email, password);

        if (!authenticated) {
            showError("Invalid email or password.");
            return;
        }

        // TODO: Navigate to dashboard scene
        // Example:
        // SceneNavigator.showDashboard();
    }

    @FXML
    private void onGoogleSignIn(ActionEvent event) {
        // TODO: Implement Google sign-in (or show info message for now)
        showError("Google sign-in is not implemented yet.");
    }

    @FXML
    private void onForgotPassword(ActionEvent event) {
        // TODO: Show forgot-password dialog or info
        showError("Password reset is not implemented yet.");
    }

    @FXML
    private void onCreateAccount(ActionEvent event) {
        // TODO: Navigate to register scene
        SceneNavigator.showRegister();
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }

    private void clearError() {
        if (errorLabel != null) {
            errorLabel.setText("");
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }
    }

    private boolean fakeAuthentication(String email, String password) {
        // Temporary simple check – replace with real logic later
        return email.equals("test@example.com") && password.equals("password");
    }
}
