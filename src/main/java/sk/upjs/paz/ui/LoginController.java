package sk.upjs.paz.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import sk.upjs.paz.entity.User;
import sk.upjs.paz.service.AuthService;
import sk.upjs.paz.service.ServiceFactory;

import java.util.prefs.Preferences;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckBox;
    @FXML private Label errorLabel;

    private final AuthService authService =
            new AuthService(ServiceFactory.INSTANCE.getUserService());


    private final Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
    private static final String PREF_REMEMBER = "rememberMe";
    private static final String PREF_EMAIL = "rememberedEmail";

    @FXML
    private void initialize() {
        clearError();

        boolean remember = prefs.getBoolean(PREF_REMEMBER, false);
        String savedEmail = prefs.get(PREF_EMAIL, "");

        if (rememberMeCheckBox != null) {
            rememberMeCheckBox.setSelected(remember);
        }
        if (remember && emailField != null && savedEmail != null && !savedEmail.isBlank()) {
            emailField.setText(savedEmail);
        }
    }

    @FXML
    private void onSignIn(ActionEvent event) {
        clearError();

        String email = safeText(emailField);
        String password = passwordField != null && passwordField.getText() != null
                ? passwordField.getText()
                : "";

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill in both email and password.");
            return;
        }

        // Remember email (save/remove)
        if (rememberMeCheckBox != null && rememberMeCheckBox.isSelected()) {
            prefs.putBoolean(PREF_REMEMBER, true);
            prefs.put(PREF_EMAIL, email);
        } else {
            prefs.putBoolean(PREF_REMEMBER, false);
            prefs.remove(PREF_EMAIL);
        }

        try {
            User u = authService.login(email, password);
            AppState.setCurrentUser(u);
            SceneNavigator.showDashboard();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    private void onCreateAccount(ActionEvent event) {
        SceneNavigator.showRegister();
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

    private String safeText(TextField field) {
        return field != null && field.getText() != null ? field.getText().trim() : "";
    }

    public void prefillEmail(String email) {
        if (emailField != null && email != null) {
            emailField.setText(email);
        }
    }
}
