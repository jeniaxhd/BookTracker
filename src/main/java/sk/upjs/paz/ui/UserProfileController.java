package sk.upjs.paz.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class UserProfileController {

    @FXML private BorderPane root;

    // Sidebar nav
    @FXML private ToggleButton dashboardNavButton;
    @FXML private ToggleButton libraryNavButton;
    @FXML private ToggleButton currentlyReadingNavButton;
    @FXML private ToggleButton statisticsNavButton;
    @FXML private ToggleButton settingsNavButton;

    // Sidebar user block
    @FXML private Button userProfileButton;
    @FXML private Label userNameLabel;
    @FXML private Label userInitialsLabel;
    @FXML private Label userSubtitleLabel;

    // Header
    @FXML private Label headerTitleLabel;
    @FXML private Label headerSubtitleLabel;

    @FXML private Button notificationsButton;
    @FXML private ImageView notificationsIcon;

    @FXML private ToggleButton themeToggle;
    @FXML private ImageView themeIcon;

    // Content fields
    @FXML private Label profileNameLabel;
    @FXML private Label emailValueLabel;
    @FXML private Label profileAvatarText;
    @FXML private ComboBox<String> languageCombo;
    @FXML private TextField dailyGoalField;

    // Icons
    private Image bellLight;
    private Image bellDark;
    private Image moonIcon;
    private Image sunIcon;

    @FXML
    private void initialize() {
        // Sidebar toggle group
        ToggleGroup navGroup = new ToggleGroup();
        dashboardNavButton.setToggleGroup(navGroup);
        libraryNavButton.setToggleGroup(navGroup);
        currentlyReadingNavButton.setToggleGroup(navGroup);
        statisticsNavButton.setToggleGroup(navGroup);
        settingsNavButton.setToggleGroup(navGroup);

        if (headerTitleLabel != null) headerTitleLabel.setText("Profile");
        if (headerSubtitleLabel != null) headerSubtitleLabel.setText("Account details and preferences");

        // Demo user text (optional)
        if (AppState.getCurrentUser() != null) {
            String name = AppState.getCurrentUser().getName();
            if (userNameLabel != null) userNameLabel.setText(name);
            if (profileNameLabel != null) profileNameLabel.setText(name);
            if (profileAvatarText != null) profileAvatarText.setText(initialsOf(name));
            if (userInitialsLabel != null) userInitialsLabel.setText(initialsOf(name));
        }

        if (userSubtitleLabel != null) userSubtitleLabel.setText("View profile");

        if (languageCombo != null) {
            languageCombo.getItems().setAll("English", "Slovak", "Ukrainian");
            languageCombo.getSelectionModel().selectFirst();
        }

        // Icons
        bellLight = load("/img/logoLight/bell.png");
        bellDark  = load("/img/logoDark/bell.png");
        moonIcon  = load("/img/logoLight/moon.png");
        sunIcon   = load("/img/logoDark/sun.png");

        // Apply theme when scene becomes available
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                ThemeManager.apply(newScene);
                updateIconsForTheme();
                SceneNavigator.attachSessionBarIfPossible(newScene.getRoot());
            }
        });

        if (root.getScene() != null) {
            ThemeManager.apply(root.getScene());
            updateIconsForTheme();
            SceneNavigator.attachSessionBarIfPossible(root.getScene().getRoot());
        }
    }

    private Image load(String path) {
        var url = getClass().getResource(path);
        return url == null ? null : new Image(url.toExternalForm());
    }

    private String initialsOf(String name) {
        if (name == null || name.trim().isEmpty()) return "U";
        String[] parts = name.trim().split("\\s+");
        String a = parts[0].substring(0, 1).toUpperCase();
        String b = parts.length > 1 ? parts[1].substring(0, 1).toUpperCase() : "";
        return (a + b);
    }

    // Theme
    @FXML
    private void onToggleTheme(ActionEvent event) {
        ThemeManager.toggle();
        ThemeManager.apply(root.getScene());
        updateIconsForTheme();
        SceneNavigator.syncFloatingOverlaysTheme();
    }

    private void updateIconsForTheme() {
        boolean dark = ThemeManager.isDarkMode();

        if (themeToggle != null) themeToggle.setSelected(dark);
        if (themeIcon != null) themeIcon.setImage(dark ? sunIcon : moonIcon);
        if (notificationsIcon != null) notificationsIcon.setImage(dark ? bellDark : bellLight);
    }

    // Notifications popover
    @FXML
    private void onNotifications(ActionEvent event) {
        SceneNavigator.toggleNotifications(notificationsButton);
    }

    // Sidebar navigation
    @FXML private void onDashboardSelected(ActionEvent e) { SceneNavigator.showDashboard(); }
    @FXML private void onLibrarySelected(ActionEvent e) { SceneNavigator.showLibrary(); }
    @FXML private void onCurrentlyReadingSelected(ActionEvent e) { SceneNavigator.showCurrentlyReading(); }
    @FXML private void onStatisticsSelected(ActionEvent e) { SceneNavigator.showStatistics(); }
    @FXML private void onSettingsSelected(ActionEvent e) { SceneNavigator.showSettings(); }

    @FXML
    private void onUserProfile(ActionEvent e) {
        // Already here
    }

    // Optional buttons (if you wired them)
    @FXML private void onEditProfile(ActionEvent e) { /* TODO */ }
    @FXML private void onLogout(ActionEvent e) { SceneNavigator.showLogin(); }
    @FXML private void onDiscardChanges(ActionEvent e) { /* TODO */ }
    @FXML private void onSaveChanges(ActionEvent e) { /* TODO */ }
}
