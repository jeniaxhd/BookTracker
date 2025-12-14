package sk.upjs.paz.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sk.upjs.paz.entity.User;
import sk.upjs.paz.service.ServiceFactory;

import java.util.Optional;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckBox;
    @FXML private Label errorLabel;

    @FXML
    private void initialize() {
        clearError();
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

        var userOpt = sk.upjs.paz.service.ServiceFactory.INSTANCE.getUserService().getByEmail(email);
        if (userOpt.isEmpty()) {
            showError("No user with this email (demo mode).");
            return;
        }

        AppState.setCurrentUser(userOpt.get());
        SceneNavigator.showDashboard();
    }


    @FXML
    private void onCreateAccount(ActionEvent event) {
        SceneNavigator.showRegister();
    }

    @FXML
    private void onGoogleSignIn(ActionEvent event) {
        showError("Google sign-in is not implemented yet.");
    }

    @FXML
    private void onForgotPassword(ActionEvent event) {
        showError("Password reset is not implemented yet.");
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
}
