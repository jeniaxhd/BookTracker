package sk.upjs.paz.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import sk.upjs.paz.entity.User;
import sk.upjs.paz.service.ServiceFactory;
import sk.upjs.paz.service.UserService;

public class UserProfileController {

    @FXML private BorderPane root;

    // sidebar user
    @FXML private Label userInitialsLabel;
    @FXML private Label userNameLabel;
    @FXML private Label userSubtitleLabel;

    // profile card
    @FXML private Label profileAvatarText;
    @FXML private Label profileNameLabel;
    @FXML private Label profileMetaLabel;   // make sure fx:id matches in FXML
    @FXML private Label emailValueLabel;

    // edit box
    @FXML private VBox editBox;
    @FXML private TextField nameEditField;

    // header
    @FXML private ToggleButton themeToggle;
    @FXML private ImageView themeIcon;

    @FXML private Button notificationsButton;
    @FXML private ImageView notificationsIcon;

    // icons
    private Image bellLight;
    private Image bellDark;
    private Image moonIcon;
    private Image sunIcon;

    private final UserService userService = ServiceFactory.INSTANCE.getUserService();

    @FXML
    private void initialize() {
        // load icons
        bellLight = load("/img/logoLight/bell.png");
        bellDark  = load("/img/logoDark/bell.png");
        moonIcon  = load("/img/logoLight/moon.png");
        sunIcon   = load("/img/logoDark/sun.png");

        // apply theme when scene appears and refresh icons
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                ThemeManager.apply(newScene);
                updateIconsForTheme();
            }
        });
        if (root.getScene() != null) {
            ThemeManager.apply(root.getScene());
            updateIconsForTheme();
        }

        loadUser();

        // hide edit box by default (optional, but usually needed)
        if (editBox != null) {
            editBox.setVisible(false);
            editBox.setManaged(false);
        }
    }

    private Image load(String path) {
        var url = getClass().getResource(path);
        return url == null ? null : new Image(url.toExternalForm());
    }

    private void updateIconsForTheme() {
        boolean dark = ThemeManager.isDarkMode();

        if (themeToggle != null) themeToggle.setSelected(dark);
        if (themeIcon != null) themeIcon.setImage(dark ? sunIcon : moonIcon);
        if (notificationsIcon != null) notificationsIcon.setImage(dark ? bellDark : bellLight);
    }

    private void loadUser() {
        User current = AppState.getCurrentUser();
        if (current == null || current.getId() == null) {
            SceneNavigator.showLogin();
            return;
        }

        User u = userService.getById(current.getId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        String name = u.getName();
        String email = u.getEmail();

        if (profileNameLabel != null) profileNameLabel.setText(name);
        if (emailValueLabel != null) emailValueLabel.setText(email);

        String initial = (name != null && !name.isBlank())
                ? String.valueOf(Character.toUpperCase(name.trim().charAt(0)))
                : "U";

        if (profileAvatarText != null) profileAvatarText.setText(initial);

        if (userInitialsLabel != null) userInitialsLabel.setText(initial);
        if (userNameLabel != null) userNameLabel.setText(name);
        if (userSubtitleLabel != null) userSubtitleLabel.setText("View profile");

        if (profileMetaLabel != null) {
            profileMetaLabel.setText("Books read: " + u.getReadBooks());
        }
    }

    // ===== Edit profile =====

    @FXML
    private void onEditProfile(ActionEvent e) {
        if (nameEditField != null && profileNameLabel != null) {
            nameEditField.setText(profileNameLabel.getText());
        }
        if (editBox != null) {
            editBox.setVisible(true);
            editBox.setManaged(true);
        }
    }

    @FXML
    private void onCancelEdit(ActionEvent e) {
        if (editBox != null) {
            editBox.setVisible(false);
            editBox.setManaged(false);
        }
    }

    @FXML
    private void onSaveProfile(ActionEvent e) {
        User current = AppState.getCurrentUser();
        if (current == null || current.getId() == null) return;

        User u = userService.getById(current.getId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        String newName = nameEditField != null && nameEditField.getText() != null
                ? nameEditField.getText().trim()
                : "";

        if (newName.isBlank()) return;

        u.setName(newName);
        userService.update(u);

        AppState.setCurrentUser(u);
        onCancelEdit(e);
        loadUser();
    }

    @FXML
    private void onLogout(ActionEvent e) {
        AppState.logout();
        SceneNavigator.showLogin();
    }

    // ===== Header actions =====

    @FXML
    private void onNotifications(ActionEvent e) {
        SceneNavigator.toggleNotifications(notificationsButton);
    }

    @FXML
    private void onToggleTheme(ActionEvent e) {
        ThemeManager.toggle();
        ThemeManager.apply(root.getScene());
        updateIconsForTheme();
        SceneNavigator.syncFloatingOverlaysTheme();
    }

    // ===== Sidebar navigation =====

    @FXML private void onDashboardSelected(ActionEvent e) { SceneNavigator.showDashboard(); }
    @FXML private void onLibrarySelected(ActionEvent e) { SceneNavigator.showLibrary(); }
    @FXML private void onCurrentlyReadingSelected(ActionEvent e) { SceneNavigator.showCurrentlyReading(); }
    @FXML private void onStatisticsSelected(ActionEvent e) { SceneNavigator.showStatistics(); }
    @FXML private void onSettingsSelected(ActionEvent e) { SceneNavigator.showSettings(); }
    @FXML private void onUserProfile(ActionEvent e) { SceneNavigator.showUserProfile(); }
}
