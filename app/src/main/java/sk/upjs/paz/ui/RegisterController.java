package sk.upjs.paz.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import sk.upjs.paz.service.AuthService;
import sk.upjs.paz.service.ServiceFactory;

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
    private final AuthService authService =
            new AuthService(ServiceFactory.INSTANCE.getUserService());

    @FXML
    private void onSignUp(ActionEvent event) {
        clearError();

        String fullName = safeText(fullNameField);
        String email = safeText(emailField);
        String pass = safeText(passwordField);
        String confirm = safeText(confirmPasswordField);

        if (fullName.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }
        if (!pass.equals(confirm)) {
            showError("Passwords do not match.");
            return;
        }
        if (pass.length() < 6) {
            showError("Password should be at least 6 characters long.");
            return;
        }

        try {

            authService.register(fullName, email, pass);


            SceneNavigator.showLogin();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
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
}
