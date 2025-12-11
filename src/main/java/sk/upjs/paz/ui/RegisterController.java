package sk.upjs.paz.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private CheckBox newsletterCheck;

    @FXML
    private Label errorLabel;

    @FXML
    private Button createAccountButton;

    @FXML
    private Button googleButton;

    @FXML
    private Hyperlink loginLink;

    @FXML
    private void initialize() {
        // Hide error label on start
        if (errorLabel != null) {
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }
    }

    @FXML
    private void onSignUp(ActionEvent event) {
        clearError();

        String fullName = safeText(fullNameField);
        String email = safeText(emailField);
        String password = safeText(passwordField);
        String confirmPassword = safeText(confirmPasswordField);

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        if (password.length() < 6) {
            showError("Password should be at least 6 characters long.");
            return;
        }

        // TODO: replace with real registration logic using database
        boolean registrationOk = fakeRegistration(email);

        if (!registrationOk) {
            showError("An account with this email already exists.");
            return;
        }

        // Navigate to dashboard after successful registration (for now)
        SceneNavigator.showDashboard();
    }
    @FXML
    private void onGoogleSignIn(ActionEvent event) {
        showError("Google sign-in is not implemented yet.");
    }

    @FXML
    private void onGoToLogin(ActionEvent event) {
        SceneNavigator.showLogin();
    }

    @FXML
    private void onGoogleSignUp(ActionEvent event) {
        showError("Google sign-up is not implemented yet.");
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

    private String safeText(TextField field) {
        return field != null && field.getText() != null ? field.getText().trim() : "";
    }

    private boolean fakeRegistration(String email) {
        // Temporary fake logic: assume "test@example.com" is already taken
        return !email.equalsIgnoreCase("test@example.com");
    }
}
